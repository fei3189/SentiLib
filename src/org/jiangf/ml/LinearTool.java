package org.jiangf.ml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import libsvm.svm;
import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;


public class LinearTool {
	Feature [][]data = null;
	double [] label = null;
	Model model = null;
	
	public LinearTool() {
	}
	public LinearTool(ArrayList<double[]> vfeatures, ArrayList<Integer> vlabel) {
		loadTrainingData(vfeatures, vlabel);
	}
	
	public void train(ArrayList<double[]> vfeatures, ArrayList<Integer> vlabel, double c) {
		loadTrainingData(vfeatures, vlabel);
		Problem problem = new Problem();
		problem.l = data.length;
		problem.n = data[0].length;
		problem.x = data;
		problem.y = label;

		SolverType solver = SolverType.L2R_LR;//L2R_LR; // -s 0
		double C = c;    // cost of constraints violation
		double eps = 0.01; // stopping criteria

		Parameter parameter = new Parameter(solver, C, eps);
		model = Linear.train(problem, parameter);
	}
	
	public ArrayList<Integer> predict(ArrayList<double[]> features) {
		ArrayList<Integer> target = new ArrayList<Integer>();
		for (int i = 0; i < features.size(); ++i) {
			Feature []vec = new FeatureNode[features.get(i).length];
			for (int j = 0; j < vec.length; ++j) {
				vec[j] = new FeatureNode(j + 1, features.get(i)[j]);
			}
			System.out.println(features.get(i).length);
			target.add((int)(Linear.predict(model, vec) * 1.001));
		}
		return target;
	}
	
	public int predict(double[] feature) {
		Feature []vec = new FeatureNode[feature.length];
		for (int j = 0; j < vec.length; ++j) {
			vec[j] = new FeatureNode(j + 1, feature[j]);
		}
		return (int)(Linear.predict(model, vec) * 1.001);
	}
	
	public void save(String path) throws IOException {
		model.save(new File(path));
	}
	
	private void loadTrainingData(ArrayList<double[]> vfeature, ArrayList<Integer> vlabel) {
		assert vfeature != null && vfeature.size() > 0 && vfeature.get(0) != null;
		assert vlabel != null;
		assert vfeature.size() == vlabel.size();
		
		label = new double[vlabel.size()];
		for (int i = 0; i < label.length; ++i)
			label[i] = vlabel.get(i);
		data = new Feature[vfeature.size()][vfeature.get(0).length];
		for (int i = 0; i < data.length; ++i)
			for (int j = 0; j < data[i].length; ++j)
				data[i][j] = new FeatureNode(j + 1, vfeature.get(i)[j]);
	}
}
