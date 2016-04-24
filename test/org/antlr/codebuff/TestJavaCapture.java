package org.antlr.codebuff;

import org.antlr.v4.runtime.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.antlr.codebuff.Trainer.ANALYSIS_START_TOKEN_INDEX;
import static org.antlr.codebuff.Trainer.getRealTokens;
import static org.junit.Assert.assertTrue;

/*
A useful measure would be "stability"; train a doc in isolation then format
that doc and measure similarity. Ideally, we'd see 0 difference but
around 0.01 would be ok not great (1%). 0.001 would be great!
We also want determinism where running format operation always gets same
result for same training corpus and test doc.
 */

@RunWith(Parameterized.class)
public class TestJavaCapture {
	public static final String ST4_CORPUS = "corpus/java/training/stringtemplate4";

	public String fileName;

	public TestJavaCapture(String fileName) {
		this.fileName = fileName;
	}

	@Test
	public void testCapture() throws Exception {
		Corpus corpus = new Corpus(fileName, ".*\\.java", Tool.JAVA_DESCR);
		corpus.train();
		InputDocument testDoc = Tool.load(fileName, corpus.language);
		Formatter formatter = new Formatter(corpus);
		String output = formatter.format(testDoc, false);
		List<TokenPositionAnalysis> analysisPerToken = formatter.getAnalysisPerToken();

		int misclassified_ws = 0;
		int misclassified_alignment = 0;

		List<Token> realTokens = getRealTokens(corpus.documentsPerExemplar.get(0).tokens);
		int n = realTokens.size();
		for (int i = ANALYSIS_START_TOKEN_INDEX; i<n; i++) { // can't process first 2 tokens
			int ws = corpus.injectWhitespace.get(i-ANALYSIS_START_TOKEN_INDEX);
			int align = corpus.align.get(i-ANALYSIS_START_TOKEN_INDEX);

			int tokenIndexInStream = realTokens.get(i).getTokenIndex();
			TokenPositionAnalysis tokenPositionAnalysis = analysisPerToken.get(tokenIndexInStream);

			if ( ws!=tokenPositionAnalysis.wsPrediction ) {
//				System.out.printf("%s ws=%d vs %d\n", realTokens.get(i).toString(), ws, tokenPositionAnalysis.ws);
				misclassified_ws++;
			}

			if ( align!=tokenPositionAnalysis.alignPrediction ) {
//				System.out.printf("%s align=%d vs %d\n", realTokens.get(i).toString(), align, tokenPositionAnalysis.align);
				misclassified_alignment++;
			}
		}
		double ws_miss_rate = (misclassified_ws*100.0)/n;
		double alignment_miss_rate = (misclassified_alignment*100.0)/n;
		System.out.printf("misclassified ws=%d (%f%%) alignment=%d (%f%%)\n",
		                  misclassified_ws, ws_miss_rate,
		                  misclassified_alignment, alignment_miss_rate);
//		System.out.println(output);
		assertTrue("Inject whitespace miss rate too high "+ws_miss_rate+"%", ws_miss_rate<5);
		assertTrue("Alignment miss rate too high "+alignment_miss_rate+"%", alignment_miss_rate<5);
	}

	@Parameterized.Parameters(name="{0}")
	public static Collection<Object[]> findInputFiles() throws Exception {
		List<Object[]> args = new ArrayList<>();
		List<String> filenames = Tool.getFilenames(new File(ST4_CORPUS), ".*\\.java");
		for (String fname : filenames) {
			args.add(new Object[] {fname});
		}
		return args;
	}
}
