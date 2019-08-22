package com.ltq.demo;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * @创建人 ltq
 * @创建时间 2019/8/16  20:17
 * @描述
 */
public class IndexManager {
    //创建索引文件
    @Test
    public void testCreateIndex() throws Exception {
        //创建索引对象 IndexWriter
             //Directory d, 索引文件位置 IndexWriterConfig conf
//        Directory directory = FSDirectory.open ( new File ( "E:\\lucene-index" ) );
        Directory directory = FSDirectory.open ( new File ( "E:\\lucene-index\\lucene-index" ) );
//        Version matchVersion, Analyzer analyzer
//        Analyzer analyzer =new StandardAnalyzer (  );
        Analyzer analyzer =new IKAnalyzer (  );//更换分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig (Version.LATEST,analyzer );
        IndexWriter indexWriter = new IndexWriter ( directory,indexWriterConfig);
//        indexWriter.deleteAll ();
        //创建文档对象
        File path = new File ( "F:\\就业101\\新版电商前置课\\day02_lucene\\资料\\上课用的查询资料searchsource" );
        File[] files = path.listFiles ( );
        for (File file : files) {
            //创建文档
            Document document = new Document ( );
            //文档属性
            String fileName = file.getName ( );
            String fileContent = FileUtils.readFileToString ( file, "utf-8" );
            long fileSize = FileUtils.sizeOf ( file );  //单位是字节
            String filePath = file.getPath ( );
            //属性与创建额文档进行关联
            document.add ( new TextField ("fileName",fileName ,Field.Store.YES ) );//原始内容 分词前的内容存储不存储
            document.add ( new TextField ("fileContent",fileContent ,Field.Store.YES ) );//原始内容 分词前的内容存储不存储
            document.add ( new LongField ("fileSize",fileSize ,Field.Store.YES ) );//原始内容 分词前的内容存储不存储
            document.add ( new StringField ("filePath",filePath ,Field.Store.YES ) );//原始内容 分词前的内容存储不存储
            //创建文档
            indexWriter.addDocument (document);
        }
        indexWriter.close ();
    } @Test
    public void testCreateIndexBoost() throws Exception {
        //创建索引对象 IndexWriter
        Directory directory = FSDirectory.open ( new File ( "E:\\lucene-index\\lucene-index" ) );
        Analyzer analyzer =new IKAnalyzer (  );//分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig (Version.LATEST,analyzer );
        IndexWriter indexWriter = new IndexWriter ( directory,indexWriterConfig);
//      创建文档对象
//        File path = new File ( "F:\\就业101\\新版电商前置课\\day02_lucene\\资料\\上课用的查询资料searchsource" );
            //创建文档
            Document document = new Document ( );
            //文档属性
            //属性与创建额文档进行关联
        TextField textField = new TextField ( "fileName", "spring是工作中一个不可少的框架", Field.Store.YES );
        textField.setBoost ( 20 );
        document.add (textField );//原始内容 分词前的内容存储不存储
            document.add ( new TextField ("fileContent","spring是工作中一个不可少的框架spring是工作中一个不可少的框架" ,Field.Store.YES ) );//原始内容 分词前的内容存储不存储
            document.add ( new LongField ("fileSize",200L ,Field.Store.YES ) );//原始内容 分词前的内容存储不存储
            document.add ( new StringField ("filePath","lllll" ,Field.Store.YES ) );//原始内容 分词前的内容存储不存储
            //创建文档
            indexWriter.addDocument (document);
        indexWriter.close ();
    }
    //查询索引
    @Test
    public void testSearch() throws Exception{
        //查询索引的位置
//        Directory directory = FSDirectory.open ( new File ( "E:\\lucene-index" ) );
        Directory directory = FSDirectory.open ( new File ( "E:\\lucene-index\\lucene-index" ) );
        //查询索引对象
        IndexReader indexReader = DirectoryReader.open ( directory );
        //搜索对象
        IndexSearcher indexSearcher = new IndexSearcher ( indexReader );
        //查询
//        Query query = new TermQuery ( new Term ( "fileName","spring" ) );//按照最小单元查询term
//        Query query = new MatchAllDocsQuery (  );//查询所有
//        Query query = new WildcardQuery ( new Term ( "fileName","*简*" ) );//模糊查询 通配符查询
//        BooleanQuery query = new BooleanQuery ( );   //组合查询 条件查询
//        query.add (new TermQuery (new Term ( "fileName","apache" )),BooleanClause.Occur.MUST );
//        query.add (new TermQuery (new Term ( "fileName","apache" )),BooleanClause.Occur.MUST );
//        NumericRangeQuery<Long> query = NumericRangeQuery.newLongRange ("fileSize",100L,1000L,true,true );//范围查询
//        Query query = NumericRangeQuery.newLongRange ("fileSize",100L,1000L,true,true );//范围查询
//        QueryParser queryParser = new QueryParser ( "fileName", new IKAnalyzer ( ) );// 单域的关键词分词查询
//        QueryParser queryParser = new MultiFieldQueryParser ( new String[]{"fileName","fileContent"}, new IKAnalyzer ( ) );// 单域的关键词分词查询
//        Query query = queryParser.parse ( "spring is a goog project" );
        Query query = new TermQuery ( new Term ( "fileName","spring" ) );//按照最小单元查询term
        TopDocs topDocs = indexSearcher.search ( query, 100 ); //最多显示条数据,相当于分页查询
        int totalHits = topDocs.totalHits;  //符合查询条件的总条数
        System.out.println ( "符合查询条件的总条数"+totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document doc = indexSearcher.doc ( docID );
            System.out.println ("fileName:"+ doc.get ( "fileName" ) );
            System.out.println ( "fileSize:"+ doc.get ( "fileSize" ) );
            System.out.println ("filePath:"+  doc.get ( "filePath" ) );
//            System.out.println ( "fileContent:"+ doc.get ( "fileContent" ) );
            System.out.println (  ( "----------------------------" ) );
        }

        indexReader.close ();
    }
    @Test
    public void testAnalyzer() throws IOException {
//        Analyzer analyzer = new StandardAnalyzer ( );
        //常用中文分词器
//        Analyzer analyzer = new CJKAnalyzer ( );    //lucene自带的中文分词器
//        Analyzer analyzer = new SmartChineseAnalyzer ( );    //lucene自带的中文分词器
        Analyzer analyzer = new IKAnalyzer ( );    //中国人自己的中文分词器
//        String text="The Spring Framework provides a comprehensive programming and configuration model.";
        String text="T他妈的F:\\就业101\\新版电商前置课\\day02_lucene\\资料\\上课用的查询资料searchsourceconfiguration model.";
        TokenStream tokenStream = analyzer.tokenStream ( "text", text );//分词方法
        //tokenStream   设置指针 地址引用
        CharTermAttribute charTermAttribute = tokenStream.addAttribute ( CharTermAttribute.class );
        //指针复位,回归初始位置
        tokenStream.reset ();
        //
        while (tokenStream.incrementToken ()){
            System.out.println ( charTermAttribute);
        }
    }
}
