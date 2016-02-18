package org.antlr.codebuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Corpus {
	List<InputDocument> documents; // an entry for each X
	List<int[]> X;
	List<Integer> injectNewlines;
	List<Integer> injectWS;
	List<Integer> indent;
	List<Integer> levelsToCommonAncestor; // steps to common ancestor whose first token is alignment anchor

	// an index to narrow down the number of vectors we compute distance() on each classification
	// map tuple (LT(-2),LT(-1),LT(1),LT(2)) to list of vector indexes with those tokens
//	Map<TokenContext, List<Integer>> tokenContextMap;

//	Map<Integer, List<Integer>> curTokenToVectorsMap;
	Map<Integer, List<Integer>> curRuleIndexToVectorsMap;

	public Corpus(List<InputDocument> documents,
				  List<int[]> X,
				  List<Integer> injectNewlines,
				  List<Integer> injectWS,
				  List<Integer> indent,
				  List<Integer> levelsToCommonAncestor)
	{
		this.documents = documents;
		this.X = X;
		this.injectNewlines = injectNewlines;
		this.injectWS = injectWS;
		this.indent = indent;
		this.levelsToCommonAncestor = levelsToCommonAncestor;

	}

	public void buildTokenContextIndex() {
		curRuleIndexToVectorsMap = new HashMap<>();
		for (int i=0; i<X.size(); i++) {
			int curRuleIndex = X.get(i)[CollectFeatures.INDEX_RULE];
			List<Integer> vectorIndexes = curRuleIndexToVectorsMap.get(curRuleIndex);
			if ( vectorIndexes==null ) {
				vectorIndexes = new ArrayList<>();
				curRuleIndexToVectorsMap.put(curRuleIndex, vectorIndexes);
			}
			vectorIndexes.add(i);
//			TokenContext ctx = new TokenContext(
//				X.get(i)[CollectFeatures.INDEX_PREV2_TYPE],
//				X.get(i)[CollectFeatures.INDEX_PREV_TYPE],
//				curTokenType,
//				X.get(i)[CollectFeatures.INDEX_NEXT_TYPE]
//			);
//			List<Integer> vectorIndexes = tokenContextMap.get(ctx);
//			if ( vectorIndexes==null ) {
//				vectorIndexes = new ArrayList<>();
//				tokenContextMap.put(ctx, vectorIndexes);
//			}
//			vectorIndexes.add(i);
		}
//		for (int ttype : curRuleIndexToVectorsMap.keySet()) {
//			List<Integer> vectorIndexes = curRuleIndexToVectorsMap.get(ttype);
//			System.out.print(JavaParser.ruleNames[ttype]+" ("+vectorIndexes.size()+"): ");
//			for (int i = 0; i<vectorIndexes.size(); i++) {
//				Integer j = vectorIndexes.get(i);
//				int ttype2 = X.get(j)[CollectFeatures.INDEX_RULE];
//				System.out.print(" "+JavaParser.ruleNames[ttype2]);
//			}
//			System.out.println();
//		}
	}
}
