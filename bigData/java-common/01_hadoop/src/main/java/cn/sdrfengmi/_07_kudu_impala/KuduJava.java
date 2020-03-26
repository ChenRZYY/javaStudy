package cn.sdrfengmi._07_kudu_impala;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class KuduJava {

    //kudu客户端
    private KuduClient client = null;
    //kudu集群的master地址
    private String KUDU_MASTER = null;
    //操作表名
    private String KUDU_TABLE = null;
    @Before
    public void init(){
        //初始化kudumaster地址
        KUDU_MASTER = "hadoop01:7051,hadoop02:7051,hadoop03:7051";

        KUDU_TABLE = "student";
        //初始化客户端
        client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
    }
    /**
     * 1、创建表
     */
    @Test
    public void createTable() throws KuduException {

        //1、创建Schema
        //创建列
        //在创建schema的时候，主键列必须在第一行
        List<ColumnSchema> columns = new ArrayList<ColumnSchema>();
        columns.add(new ColumnSchema.ColumnSchemaBuilder("id", Type.INT32).key(true).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("name", Type.STRING).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("age", Type.INT32).build());
        Schema schema = new Schema(columns);
        //2、创建表的属性信息:[tablet的个数<分区数>,分区字段，tablet的副本数]
        CreateTableOptions options = new CreateTableOptions();
        //指定分区字段与分区的个数
        ArrayList<String> partitionColumns = new ArrayList<String>();
        //分区字段必须是主键字段
        partitionColumns.add("id");
        //分区数设置的时候必须>=2
        options.addHashPartitions(partitionColumns,2);
        //指定副本数
        //设置副本数的时候，副本的数量必须<=活着的tabletserver的个数
        options.setNumReplicas(1);
        //3、创建表
        client.createTable(KUDU_TABLE,schema,options);
    }

    /**
     * 2、数据插入kudu
     */
    @Test
    public void insert() throws KuduException {
        //1、打开表
        KuduTable table = client.openTable(KUDU_TABLE);
        //2、创建session
        KuduSession session = client.newSession();
        //手动刷新
        session.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
        //设置缓存的数据条数，当数据达到指定数量之后，会自动刷新
        //session.setMutationBufferSpace(100);
        //隔多长时间刷新一次，默认单位是毫秒
        //session.setFlushInterval(5000);
        //3、创建insert对象
        //4、构建插入数据
        for(int i=0;i<=10;i++){
            Insert insert = table.newInsert();

            insert.getRow().addInt("id",i);
            insert.getRow().addString("name","zhangsan-"+i);
            insert.getRow().addInt("age",20+i);
            //5、数据插入
            session.apply(insert);
        }
        session.flush();
        //6、资源关闭
        session.close();
    }

    /**
     * 3、数据查询
     * select * from table where id=8
     */
    @Test
    public void query() throws KuduException {

        //1、打开表
        KuduTable kuduTable = client.openTable(KUDU_TABLE);
        //2、创建scanner扫描器
        KuduScanner.KuduScannerBuilder builder = client.newScannerBuilder(kuduTable);
        //3、设置查询条件
        //设置条件列
        //ColumnSchema columnSchema = new ColumnSchema.ColumnSchemaBuilder("id", Type.INT32).build();
        //KuduPredicate predicate = KuduPredicate.newComparisonPredicate(columnSchema, KuduPredicate.ComparisonOp.EQUAL, 4);
        //KuduScanner scanner = builder.addPredicate(predicate).build();

        //查询所有
        KuduScanner scanner = builder.build();

        //4、执行查询，打印结果
        while (scanner.hasMoreRows()){
            RowResultIterator rowResults = scanner.nextRows();
            while (rowResults.hasNext()){
                RowResult row = rowResults.next();
                int id = row.getInt("id");
                String name = row.getString("name");
                int age = row.getInt("age");
                System.out.println("id="+id+",name="+name+",age="+age);
            }
        }

    }

    /**
     * 4、数据更新
     *   更新的时候必须带上主键字段
     */
    @Test
    public void update() throws KuduException {
        //1、打开表
        KuduTable table = client.openTable(KUDU_TABLE);
        //2、创建session对象
        KuduSession session = client.newSession();
        //设置为手动刷新
        session.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
        //3、创建update对象
        Update update = table.newUpdate();

        //4、构建更新数据
        update.getRow().addInt("id",1);
        update.getRow().addString("name","lisi");
        //5、数据更新
        session.apply(update);
        session.flush();
        //6、关闭session
        session.close();
    }

    /**
     * 5、数据删除
     *   删除的时候只能用主键字段，如果加上非主键字段，删除无效
     */
    @Test
    public void delete() throws KuduException {
        //1、打开表
        KuduTable table = client.openTable(KUDU_TABLE);
        //2、创建session对象
        KuduSession session = client.newSession();
        session.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
        //3、创建delete对象
        Delete delete = table.newDelete();
        //4、构建删除数据
        delete.getRow().addInt("id",1);
        //5、删除
        session.apply(delete);
        //6、关闭session
        session.close();
    }

    /**
     * 6、数据的更新或者插入
     */
    @Test
    public void upsert() throws KuduException {
        //1、打开表
        KuduTable table = client.openTable(KUDU_TABLE);
        //2、创建session
        KuduSession session = client.newSession();
        session.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
        //3、创建Upsert对象

        //4、构建数据
        for(int i= 0;i<=1;i++){
            Upsert upsert = table.newUpsert();
            upsert.getRow().addInt("id",i);
            upsert.getRow().addString("name","lisi-"+i);
            upsert.getRow().addInt("age",55+i);
        //5、更新或者插入
            session.apply(upsert);
        }
        session.flush();

        //6、关闭session
        session.close();
    }

    /**
     * 7、表的删除
     */
    @Test
    public void drop() throws KuduException {
        //判断表是否存在
        if(client.tableExists(KUDU_TABLE)){
            client.deleteTable(KUDU_TABLE);
        }
    }
}
