package com.afaraut.ImageDatasets;
import eval.Eval;
import utils.GlobalesConstantes.SOCIAL_NETWORK;

public class MainEval {
	public static void main(String args[]) throws Exception {
		Eval.makeEvalDB(SOCIAL_NETWORK.INSTAGRAM);
	}
}

