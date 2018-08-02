package cn.itheima.lucene;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.hankcs.lucene.HanLPAnalyzer;

public class CreateIndex {
	//创建索引库的方法
	@Test
	public void creatIndex() {
		try {
			//创建写入索引库的对象
			//左边eclipse加入注释,右边修改注释
			Directory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
			//使用的分词器
			Analyzer analyzer = new HanLPAnalyzer();
			IndexWriterConfig conf = new IndexWriterConfig(analyzer);
			IndexWriter write = new IndexWriter(directory, conf);
			//读取原始的数据
			//获取当前文件所在的文件夹
			File fileDir = new File("D:\\就业班\\day67_lucene\\资料\\searchsource");
			//获取文件夹下所有文件
			File[] files = fileDir.listFiles();
			int i = 0;
			for (File file : files) {
				System.out.println("当前的文件名为:"+file.getName());
				System.out.println("当前的文件的路径为:"+file.getPath());
				System.out.println("当前的文件内容为:"+FileUtils.readFileToString(file));
				System.out.println("当前的文件的大小为:"+FileUtils.sizeOf(file));
				//将数据封装到document
				Document document = new Document();
				document.add(new StringField("fileNum", "000000"+i, Store.YES));
				document.add(new TextField("fileName",file.getName(),Store.YES));
				document.add(new TextField("fileContent",FileUtils.readFileToString(file),Store.YES));
				document.add(new StoredField("filePath", file.getPath()));
				document.add(new LongPoint("fileSize", FileUtils.sizeOf(file)));
				write.addDocument(document);
				i++;
				}
			//写入文档到索引库
			write.commit();
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//修改索引库的方法
	@Test
	public void updateIndex() {
		try {
			//创建写入索引库的对象
			Directory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
			//使用的分词器
			Analyzer analyzer = new HanLPAnalyzer();
			IndexWriterConfig conf = new IndexWriterConfig(analyzer);
			IndexWriter write = new IndexWriter(directory, conf);
			Document doc = new Document();
			doc.add(new TextField("fileName", "测试修改为1",Store.YES));
			write.updateDocument(new Term("fileNum","0000001"), doc);
			write.commit();
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//删除索引库的方法
	@Test
	public void deleteIndex() {
		try {
			//创建写入索引库的对象
			Directory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
			//使用的分词器
			Analyzer analyzer = new HanLPAnalyzer();
			IndexWriterConfig conf = new IndexWriterConfig(analyzer);
			IndexWriter write = new IndexWriter(directory, conf);
			//删除所有
			write.deleteAll();
			//删除文档,根据query结果删除
			//write.deleteDocuments(new Term("fileNum","00000014"));
			write.commit();
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
