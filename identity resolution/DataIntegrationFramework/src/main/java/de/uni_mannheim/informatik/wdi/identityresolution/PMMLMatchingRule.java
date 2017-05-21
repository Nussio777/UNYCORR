/** 
 *
 * Copyright (C) 2015 Data and Web Science Group, University of Mannheim, Germany (code@dwslab.de)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.uni_mannheim.informatik.wdi.identityresolution;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.xerces.impl.XMLEntityManager.Entity;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Computable;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.HasEntityId;
import org.jpmml.evaluator.HasEntityRegistry;
import org.jpmml.evaluator.HasProbability;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.ProbabilityDistribution;
import org.jpmml.evaluator.TargetField;
import org.jpmml.evaluator.tree.NodeScoreDistribution;
import org.xml.sax.SAXException;

import com.google.common.collect.BiMap;
import com.hp.hpl.jena.util.FileManager;

import de.uni_mannheim.informatik.wdi.model.DefaultRecord;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.Pair;

/**
 * A {@link MatchingRule} that is defined by a weighted additive linear
 * combination of attribute similarities.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @author Simon Geisler (simon-geisler@t-online.de)
 * 
 * @param <RecordType>
 */
public class PMMLMatchingRule<RecordType extends Matchable>
		extends MatchingRule<RecordType> {

	/**
	 * list of comparators. This list must not be changed after learning a model!
	 */
	private List<Comparator<RecordType>> comparators;
	
	/**
	 * PMML evaluator
	 */
	Evaluator evaluator = null;
	
	/**
	 * Target field of the PMML model
	 */
	String targetField = "label";

	/**
	 * Initializes the rule. The pmmlPath determines the PMML file to load
	 * 
	 * @param finalThreshold
	 * @param pmmlPath if you pass a zero all comparator will be weighted equally
	 */
	public PMMLMatchingRule(double finalThreshold, String pmmlPath) {
		super(finalThreshold);
		comparators = new LinkedList<>();
		init(pmmlPath);
	}
	
	/**
	 * Initializes the rule. The pmmlPath determines the PMML file to load
	 * 
	 * @param finalThreshold
	 * @param pmmlPath if you pass a zero all comparator will be weighted equally
	 * @param targetField if the target field of the PMML model differs from "label"
	 */
	public PMMLMatchingRule(double finalThreshold, String pmmlPath, String targetField) {
		super(finalThreshold);
		comparators = new LinkedList<>();
		this.targetField = targetField;
		init(pmmlPath);
	}
	
	/**
	 * For loading the PMML model
	 * @param pmmlPath
	 */
	private void init(String pmmlPath) {
		if(pmmlPath != null) {
			try(InputStream is = FileManager.get().open(pmmlPath)) {
				PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(is);
				ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
				evaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a comparator with the specified weight to this rule.
	 * 
	 * @param comparator
	 * @param weight
	 *            a double value larger than 0.
	 * @throws Exception
	 */
	public void addComparator(Comparator<RecordType> comparator) throws Exception {
		comparators.add(comparator);
	}

	@Override
	public double compare(RecordType record1, RecordType record2) {
		// if no evaluator is know set equal weights of the similarities
		if(evaluator == null) {
			double sum = 0.0;
			for (int i = 0; i < comparators.size(); i++) {
	
				Comparator<RecordType> comp = comparators.get(i);
	
				double similarity = comp.compare(record1, record2);
				sum += similarity;
			}
	
			sum /= comparators.size();
			return sum;
		} else {
			// Calculate the similarities for the records
			Map<String, Double> similarities = new HashMap<>();
			for (int i = 0; i < comparators.size(); i++) {
				Comparator<RecordType> comp = comparators.get(i);

				double similarity = comp.compare(record1, record2);

				String name = String.format("[%d] %s", i, comp.getClass().getSimpleName());
				
				similarities.put(name, similarity);
			}
			
			// Map the similarities to the input fields of the PMML model
			Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
			List<InputField> inputFields = evaluator.getInputFields();
			for(InputField inputField : inputFields){
				FieldName inputFieldName = inputField.getName();
				
				// The raw (ie. user-supplied) value could be any Java primitive value
				Object rawValue = similarities.get(inputFieldName.toString());

				// The raw value is passed through: 1) outlier treatment, 2) missing value treatment, 3) invalid value treatment and 4) type conversion
				FieldValue inputFieldValue = inputField.prepare(rawValue);

				arguments.put(inputFieldName, inputFieldValue);
			}
			
			// Execute the model
			Map<FieldName, ?> results = evaluator.evaluate(arguments);
			
			// Extract the output
			Map<String, Object> parsedResults = new HashMap<>();
			List<TargetField> targetFields = evaluator.getTargetFields();
			for(TargetField targetField : targetFields){
				FieldName targetFieldName = targetField.getName();

				Object targetFieldValue = results.get(targetFieldName);
				parsedResults.put(targetFieldName.toString(), targetFieldValue);
			}
			
//			// Return probability if available
//			if(parsedResults.get(targetField) instanceof Computable){
//				Computable computable = (Computable) parsedResults.get(targetField);
//
//				Object unboxedTargetFieldValue = computable.getResult();
//			}
//			// Test for "entityId" result feature
//			if(parsedResults.get(targetField) instanceof HasEntityId){
//				HasEntityId hasEntityId = (HasEntityId) parsedResults.get(targetField);
//				HasEntityRegistry<?> hasEntityRegistry = (HasEntityRegistry<?>) evaluator;
//			}
			
			// Check if the output has a probability
			if(parsedResults.get(targetField) instanceof HasProbability) {
				return ((HasProbability) parsedResults.get(targetField)).getProbability("true");
			}
			// Return value of the regression else
			return (double) parsedResults.get(targetField);
		}
		
	}

	@Override
	public DefaultRecord generateFeatures(RecordType record1, RecordType record2) {
		DefaultRecord model = new DefaultRecord(String.format("%s-%s",
				record1.getIdentifier(), record2.getIdentifier()), this
				.getClass().getSimpleName());

		double sum = 0;

		for (int i = 0; i < comparators.size(); i++) {
			Comparator<RecordType> comp = comparators.get(i);

			double similarity = comp.compare(record1, record2);

			sum += similarity;

			String name = String.format("[%d] %s", i, comp.getClass()
					.getSimpleName());
			model.setValue(name, Double.toString(similarity));
		}

		sum /= comparators.size();
		
		model.setValue("finalValue", Double.toString(sum));
		model.setValue("isMatch", Boolean.toString(sum >= getFinalThreshold()));

		return model;
	}
}
