package com.example.lucene;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class TikaDocumentSearch {

	private static final Path indexDirectoryPath = Path.of("/home/alexander/workspace/project/test-spring/data/lucene/index/tika");
	private static final Path[] documents = new Path[] {
			Path.of("/home/alexander/tikatest/Object Oriented Software Construction-Meyer.pdf"),
			Path.of("/home/alexander/tikatest/GNU Assembler.pdf"),
			Path.of("/home/alexander/tikatest/Motion compensated video coding.pdf"),
			Path.of("/home/alexander/tikatest/Debugging using DWARF-2012.pdf")
	};


	public static void main(String[] args) throws Exception {
//		indexDocumentTest();
		searchDocumentsTest("assembly");
	}


	public static void indexDocumentTest() throws Exception {
		Tika tika = new Tika();
		Directory indexDirectory = FSDirectory.open(indexDirectoryPath);
		IndexWriterConfig writerConfig = new IndexWriterConfig(new EnglishAnalyzer());
		IndexWriter writer = new IndexWriter(indexDirectory, writerConfig);

		for (Path documentPath : documents) {
			log.info("Indexing document: {}", documentPath);

			URLConnection connection = documentPath.toUri().toURL().openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();

			Document document = new Document();
			document.add(new TextField("filename", documentPath.toString().replace("\\s+", "_"), Field.Store.YES));
//			document.add(new TextField("fulltext", tika.parseToString(is), Field.Store.NO));
			document.add(new TextField("fulltext", tika.parse(is)));
			writer.addDocument(document);
			writer.flush();
			writer.commit();
			is.close();

			log.info("Document indexing finished");
		}

		writer.close();
		indexDirectory.close();
	}



	public static void searchDocumentsTest(String userQuery) throws Exception {

		Directory indexDirectory = FSDirectory.open(indexDirectoryPath);
		IndexReader reader = DirectoryReader.open(indexDirectory);
		IndexSearcher searcher = new IndexSearcher(reader);

		QueryParser queryParser = new QueryParser("fulltext", new EnglishAnalyzer());
		Query query = queryParser.parse(userQuery);

		int numResults = 10;
		TopDocs topDocs = searcher.search(query, numResults);

		log.info("Total Results: " + topDocs.totalHits);
		log.info("Search Results:");

		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
			Document doc = searcher.storedFields().document(scoreDoc.doc);
			log.info("Document {} - filename: {}, Score: {}", i, doc.get("filename"), scoreDoc.score);
		}

		reader.close();
		indexDirectory.close();
	}

}
