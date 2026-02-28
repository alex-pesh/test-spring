package com.example.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;

public class SearchExample {
	private static final String indexDirectoryPath = "/home/alexander/workspace/project/test-spring/data/lucene/index";

	public static void main(String[] args) throws Exception {
		indexStringsTest();
		searchStringsTest();
	}


	public static void searchStringsTest() throws Exception {

		String userQuery = "content:fucking";

		Directory indexDirectory = FSDirectory.open(Path.of(indexDirectoryPath));
		IndexReader reader = DirectoryReader.open(indexDirectory);
		IndexSearcher searcher = new IndexSearcher(reader);

		QueryParser queryParser = new QueryParser("title", new StandardAnalyzer());
		Query query = queryParser.parse(userQuery);

		int numResults = 10;
		TopDocs topDocs = searcher.search(query, numResults);

		System.out.println("Total Results: " + topDocs.totalHits);
		System.out.println("Search Results:");

		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
//			Document doc = searcher.doc(scoreDoc.doc);
			Document doc = searcher.storedFields().document(scoreDoc.doc);
			System.out.println("Document " + i + " - Title: " + doc.get("title") + ", Score: " + scoreDoc.score);
		}

		reader.close();
		indexDirectory.close();
	}


	public static void indexStringsTest() throws IOException {

		Directory indexDirectory = FSDirectory.open(Path.of(indexDirectoryPath));
		IndexWriterConfig writerConfig = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter writer = new IndexWriter(indexDirectory, writerConfig);

		String[] ids = new String[]{"0", "1", "2"};
		String[] titles = new String[]{"finance", "education", "travel"};
		int[] years = new int[]{2001, 1998, 2002};
		String[] content = new String[] {
				"Blackrock is the biggest hedge fund in fucking USA",
				"The most used programming language in the world is fucking java.",
				"Italy is the most fucking to visit in summer."
		};

		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new TextField("id", ids[i], Field.Store.YES));
			doc.add(new TextField("title", titles[i], Field.Store.YES));
			doc.add(new TextField("content", content[i], Field.Store.NO));
			doc.add(new NumericDocValuesField("year", years[i]));
			writer.addDocument(doc);
		}

		writer.close();
		indexDirectory.close();
	}

}
