package cn.itheima.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.hankcs.lucene.HanLPAnalyzer;

public class QueryIndex {
	//查询索引库的全部数据
	@Test
	public void queryAllIndex() {
		try {
			//不传递参数,搜索所有的数据
			Query query = new MatchAllDocsQuery();
			
			doQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//通过范围查询索引库的数据 根据范围查询
	//搜索文件大小1~50字节
		@Test
		public void queryIndexBySize() {
			try {
				Query query = LongPoint.newRangeQuery("fileSize", 11, 50);
				
				doQuery(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		//通过词条查询索引库的数据 根据最小单元词条查询
		@Test
		public void queryIndexByTerm() {
			try {
				Query query = new TermQuery(new Term("fileName", "springMvc原理"));
				
				doQuery(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*
		 * 组合查询
		 */
		@Test
		public void queryIndexByBoolean() {
			try {
				Query query = new TermQuery(new Term("fileName", "不明觉厉"));
				Query query2 = new TermQuery(new Term("fileName", "传智播客"));
				//设置query1的查询限制
				BooleanClause bc1 = new BooleanClause(query, Occur.MUST);
				BooleanClause bc2 = new BooleanClause(query2, Occur.MUST_NOT);
				BooleanQuery bq = new BooleanQuery.Builder().add(bc1).add(bc2).build();
				doQuery(bq);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*
		 * 通过字符串搜索匹配的数据
		 * 域字段解析查询
		 */
		@Test
		public void queryIndexByField() {
			try {
				String seacherStr = "传智播客解释全文检索的意义";
				QueryParser parser = new QueryParser("fileName",new HanLPAnalyzer()); 
				Query query = parser.parse(seacherStr);
				doQuery(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		
		/*
		 * 通过字符串搜索匹配的数据
		 * 多域字段解析查询
		 */
		@Test
		public void queryIndexByMultiField() {
			try {
				String seacherStr = "传智播客公司";
				String[] fields = new String[] {"fileName","fileContent"};
				MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new HanLPAnalyzer());
				Query query = parser.parse(seacherStr);
				doQuery(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	//通用的查询方法
	private void doQuery(Query query) throws IOException {
		//创建查询使用的对象 IndexSearcher
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get("D:\\luceneIndex")));
		IndexSearcher searcher = new IndexSearcher(indexReader); 
		//参数1为执行的query对象,参数2为返回的结果集数量
		TopDocs topDocs = searcher.search(query, 100);
		System.out.println("总命中的文档数量为:===>"+topDocs.totalHits);
		//返回每个文档的分值 影响文档显示数据的排序和id数值
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println("当前文档的id为:===>"+scoreDoc.doc);
			System.out.println("当前文档的得分为:===>"+scoreDoc.score);
			//通过文档的id提取具体的文档数据
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println("文档的名称为;"+doc.get("fileName"));
			System.out.println("文档的路径为"+doc.get("filePath"));
			System.out.println("文档的大小为:"+doc.get("fileSize"));
		}
	}
	
}
