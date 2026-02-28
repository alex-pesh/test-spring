package com.example.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class QueryParserExample {

	public static void main(String[] args) throws Exception {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		QueryParser queryParser = new QueryParser("content", analyzer);

		String userQuery = "(content:BlackRock OR content:programming) AND title:finance AND year:[2000 TO 2020]";
		Query query = queryParser.parse(userQuery);

		System.out.println("Parsed Query: " + query.toString());
	}

}
