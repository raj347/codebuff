package org.antlr.codebuff;

import java.util.List;

public class Neighbor {
	public Corpus corpus;
	public final double distance;
	public final int corpusVectorIndex; // refers to both X (independent) and Y (dependent/predictor) variables

	public Neighbor(Corpus corpus, double distance, int corpusVectorIndex) {
		this.corpus = corpus;
		this.distance = distance;
		this.corpusVectorIndex = corpusVectorIndex;
	}

	public String toString(FeatureMetaData[] FEATURES, List<Integer> Y) {
		int[] X = corpus.featureVectors.get(corpusVectorIndex);
		InputDocument doc = corpus.documentsPerExemplar.get(corpusVectorIndex);
		String features = Trainer._toString(FEATURES, doc, X);
		int line = X[Trainer.INDEX_INFO_LINE];
		String lineText = doc.getLine(line);
		int col = X[Trainer.INDEX_INFO_CHARPOS];
		// insert a dot right before char position
		if ( lineText!=null ) {
			lineText = lineText.substring(0, col)+'\u00B7'+lineText.substring(col, lineText.length());
		}
		int cat = Y.get(corpusVectorIndex);
		int[] elements = Trainer.triple(cat);
		String display = String.format("%d|%d|%d", cat&0xFF, elements[0], elements[1]);
		return String.format("%s (cat=%s,d=%1.3f): %s", features, display, distance, lineText);
	}
}
