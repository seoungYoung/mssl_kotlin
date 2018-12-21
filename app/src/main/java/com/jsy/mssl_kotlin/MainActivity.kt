package org.jsy.mssl_kotlin



import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telecom.Call
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import org.json.JSONArray
import java.io.IOException


class MainActivity : AppCompatActivity() {
    var listData =  ArrayList<HashMap<String, Any>>()
    var totalData =  ArrayList<HashMap<String, Any>>()
    var pageNum:Int=0


    var permissions_list= arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )

// commit test
    companion object {
        var totalPage:Int=-1
        const val numbersPerPage:Int=20
        // 외부
        const val SITE:String="http://122.37.216.171:12330"
        // 내부
//        const val SITE:String="http://192.168.219.104"
    }
    fun dataConditionLoad(cond: String) {
        getTotalDataCountLocalDB(cond)
        getDataLoadLocalDB(pageNum*MainActivity.numbersPerPage,cond)
        var adapter=main_list.adapter as ListAdapter
        adapter.notifyDataSetChanged()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(MainActivity.totalPage==-1) {
            // 로컬에서 총 레코드 갯수 가져오기
            getTotalDataCountLocalDB()
        }
        this.janreGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.allRb -> dataConditionLoad("1")
                R.id.allRb -> dataConditionLoad("1")
            }
        }
        this.balladeBtn.setOnClickListener {
            this.dataConditionLoad("발라드")
        }
        this.popBtn.setOnClickListener {
            this.dataConditionLoad("팝")
        }
        this.danceBtn.setOnClickListener {
            this.dataConditionLoad("댄스")
        }
        this.trotBtn.setOnClickListener {
            this.dataConditionLoad("트로트")
        }
        this.allBtn.setOnClickListener {
            this.dataConditionLoad("1")
        }
        button1.setOnClickListener {
            var map2=listData.get(0)
            var tt=map2.get("msslData") as DataContents
            Log.d("test11",tt._songEtc)
        }
        button4.isEnabled=false
        button.setOnClickListener {
            var helper:DBHelper = DBHelper(this)
            var db: SQLiteDatabase = helper.writableDatabase

            var sql = "select * from msslTable"
            var cu:Cursor=db.rawQuery(sql,null)
            var cnt=cu.count
//        Log.d("test7",cnt.toString())
            textView3.text=cnt.toString()
            db.close()
        }
        button4.setOnClickListener {
            makeLocalSQL()
        }
        button5.setOnClickListener {
            var thread=getTotalDataThread()
            thread.start()
        }
        button2.setOnClickListener {
            var helper:DBHelper = DBHelper(this)
            var db: SQLiteDatabase = helper.writableDatabase

            var sql = "delete from msslTable where ?"
            var args= arrayOf("1")
            db.execSQL(sql,args)
            db.close()

            init()
//            var sql = "delete from msslTable"
//            db.delete(sql,null,null)
            toast("모두 삭제 했습니다.")
        }
        prevPageBtn.setOnClickListener {
            if(pageNum>0) {
                pageNum--
                nextPageBtn.isEnabled=true
            }
            else {
                prevPageBtn.isEnabled=false
//                prevPageBtn.visibility
                pageNum=0
            }
            getDataLoadLocalDB(pageNum*MainActivity.numbersPerPage)
        }
        nextPageBtn.setOnClickListener {
            //            Log.d("test3",totalPage.toString())
            if(pageNum<MainActivity.totalPage) {
                pageNum++
                prevPageBtn.isEnabled=true
//                prevPageBtn.visibility=View.INVISIBLE
//                prevPageBtn.visibility=View.VISIBLE
            }
            else {
                pageNum=MainActivity.totalPage
                nextPageBtn.isEnabled=false
            }
//            Log.d("test3",pageNum.toString())
            getDataLoadLocalDB(pageNum*MainActivity.numbersPerPage)
//            var thread = getDataThread()
//            thread.start()
        }
        var adapter=ListAdapter()
        main_list.adapter=adapter

        init()
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_write->{
                var write_intent= Intent(this, WriteActivity::class.java)
                startActivity(write_intent)
            }
            R.id.menu_reload-> {
//                var thread=getDataThread()
//                thread.start()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }
    override fun onResume() {
        super.onResume()
        init()
        main_list.setOnItemClickListener { parent, view, position, id ->
            var detail_intent=Intent(this, DetailActivity::class.java)

            var map = listData.get(position) as HashMap<String, Any>
            var tt=map.get("msslData") as DataContents

            detail_intent.putExtra("tId",tt._songId)
            detail_intent.putExtra("tName",tt._songSubject)
            detail_intent.putExtra("tPhone", tt._songNumber)
            detail_intent.putExtra("tPrimeNum",tt._songKind )
            detail_intent.putExtra("tPrivateNum", tt._songEtc)
            detail_intent.putExtra("songSite", tt._songSite)

            startActivity(detail_intent)
        }
        main_list.setOnItemLongClickListener { parent, view, position, id ->
            var edit_intent=Intent(this, EditActivity::class.java)

            var map = listData.get(position) as HashMap<String, Any>
            var tt=map.get("msslData") as DataContents

            edit_intent.putExtra("tName",tt._songSubject)
            edit_intent.putExtra("tPhone", tt._songNumber)
            edit_intent.putExtra("tPrimeNum",tt._songKind )
            edit_intent.putExtra("tPrivateNum", tt._songEtc)
            edit_intent.putExtra("songSite", tt._songSite)

            startActivity(edit_intent)
//            Log.d("test",tName)
            true
        }
    }

    fun checkPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        for(permission : String in permissions_list){
            var chk = checkCallingOrSelfPermission(permission)
            if(chk == PackageManager.PERMISSION_DENIED){
                requestPermissions(permissions_list, 0);
                break;
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var idx = 0;
//        viewTxt.text = ""
        for(idx in grantResults.indices){
            if(grantResults[idx] == PackageManager.PERMISSION_GRANTED){
//                viewTxt.append("${permissions_list[idx]} : 허용함\n");
            } else {
//                viewTxt.append("${permissions_list[idx]} : 허용하지 않음\n");
            }
        }
    }
    fun init() {
        getTotalDataCountLocalDB()
        getDataLoadLocalDB(pageNum*MainActivity.numbersPerPage)
        var adapter=main_list.adapter as ListAdapter
        adapter.notifyDataSetChanged()
    }
    fun getTotalDataCountLocalDB(condition:String="1") {
        var helper:DBHelper = DBHelper(this)
        var db: SQLiteDatabase = helper.writableDatabase
        var where:String=""
        if(condition=="1") {
            where=" 1 "
        }
        else {
            where=" songKind='${condition}' "
        }
        var sql = "select * from msslTable where ${where}"
        var cu:Cursor=db.rawQuery(sql,null)
        var cnt=cu.count
//        Log.d("test7",cnt.toString())
        textView3.text=cnt.toString()
        db.close()
        MainActivity.totalPage=cnt/MainActivity.numbersPerPage //20
    }

    inner class ListAdapter: BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convert=convertView
            if(convert==null) {
                convert=layoutInflater.inflate(R.layout.row,null)
            }
            var str1=convert?.findViewById<TextView>(R.id.tIdTxt)
            var str2=convert?.findViewById<TextView>(R.id.tNameTxt)
            var str3=convert?.findViewById<TextView>(R.id.tPhoneTxt)

            var map=listData.get(position)
            var tt=map.get("msslData") as DataContents

//            Log.d("test1",map.toString())
            var id = tt._songId
            var tName =tt._songSubject
            var tPhone = tt._songNumber

            str1?.text="[$id]. "
            str2?.text=tName
            str3?.text=tPhone

            return convert!!
        }
        override fun getItem(position: Int): Any {
            return 0
        }
        override fun getItemId(position: Int): Long {
            return 0
        }
        override fun getCount(): Int {
            return listData.size
        }
    }
    fun getDataLoadLocalDB(pn:Int,condition:String="1") {
        var helper:DBHelper = DBHelper(this)
        var db: SQLiteDatabase = helper.writableDatabase
        if(db==null) {
            Log.d("test7","no DB")
            return
        }
//            Log.d("test6",db.path)
        listData.clear()
        var where:String=""
        if(condition=="1") {
            where=" 1 "
        }
        else {
            where=" songKind='${condition}' "
        }
        var sql = "select * from msslTable where ${where} limit $pn, ${MainActivity.numbersPerPage} "
        var c: Cursor = db.rawQuery(sql, null)

        while(c.moveToNext()){
            var map = HashMap<String, Any>()

            var idx_pos = c.getColumnIndex("songId")
            var tName_pos = c.getColumnIndex("songSubject")
            var tPhone_pos=c.getColumnIndex("songNumber")
            var tPrimeNum_pos=c.getColumnIndex("songKind")
            var tPrivateNum_pos=c.getColumnIndex("songEtc")
            var songSite_pos=c.getColumnIndex("songSite")
//                Log.d("test6",idx_pos.toString() + textData_pos)

            var idx = c.getInt(idx_pos)
            var tName_data = c.getString(tName_pos)
            var tPhone_data=c.getString(tPhone_pos)
            var tPrimeNum_data=c.getString(tPrimeNum_pos)
            var tPrivateNum_data=c.getString(tPrivateNum_pos)
            var songSite_data=c.getString(songSite_pos)

            var tt:DataContents= DataContents(idx.toString(),tName_data,
                    tPhone_data,tPrimeNum_data,tPrivateNum_data,songSite_data)

            map.put("msslData",tt)
            listData.add(map)
//            textView3.append("idx : ${idx}\n")
//            textView3.append("textData : ${tName_data}\n")
//            Log.d("test13",tPrivateNum_data)

        }
        db.close()

        var adapter=main_list.adapter as ListAdapter
        adapter.notifyDataSetChanged()
    }

    fun makeLocalSQL() {
//        Log.d("test5","SQl")
        var helper = DBHelper(this)
        var db = helper.writableDatabase
        var sql:String="DROP TABLE IF EXISTS msslTable"
        db.execSQL(sql)

        sql = "create table msslTable (" +
                "songId integer primary key autoincrement, " +
                "songSubject text not null, " +
                "songNumber text not null, " +
                "songKind text not null, " +
                "songEtc text not null, " +
                "songSite text not null " +
                ")"
        db.execSQL(sql)

        var map = HashMap<String, Any>()

        for(i in 0 until totalData.size) {
            map= totalData.get(i)
//            var cv = ContentValues()
            var tt:DataContents=map.get("msslData") as DataContents

            sql="insert into msslTable ('songSubject','songNumber','songKind','songEtc','songSite') values(?, ?, ?, ?,?)"
            var arg1 = arrayOf("${tt._songSubject}","${tt._songNumber}","${tt._songKind}","${tt._songEtc}","${tt._songSite}")
//            Log.d("test11",tt._songEtc)
            db.execSQL(sql,arg1)
//            db.rawQuery(sql,null)
//            cv.put("songId",  tt._songId)
//            cv.put("songSubject",  tt._songSubject)
//            cv.put("songNumber",  tt._songNumber)
//            cv.put("songKind",  tt._songKind)
//            cv.put("songEtc",  tt._songEtc)
//            Log.d("test9",tt._songSubject)
//            db.insert("msslTable", null, cv)
        }
        db.close()
        textView3.text="저장완료"

        alert(title="확인",message = "테스트용 데이터를 만들었습니다") {
            positiveButton("확인") {

            }
        }.show()
        onResume()
    }
    inner class getTotalDataThread():Thread() {
        override fun run() {
            var client = OkHttpClient()
            var builder = Request.Builder()
            var url = builder.url("${MainActivity.SITE}/kotlin_mssl/loadTotalData.php")
//            var url = builder.url("http://192.168.219.104/kotlin_mssl/loadTotalData.php")
//            var url = builder.url("http://122.37.216.171:12330/kotlin_mssl/loadTotalData.php")
            var request = url.build()
            var callback = Callback1()

            client.newCall(request).enqueue(callback)
        }
    }
    inner class Callback1():Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            runOnUiThread {
                alert(title="확인",message = "가져오기에 실패했습니다.\n인터넷 상태를 확인하세요!") {
                    positiveButton("확인") {

                    }
                }.show()
                textView3.text="Fail!!"
            }
        }
        override fun onResponse(call: okhttp3.Call?, response: Response?) {
            //            Log.d("test8","success!")
            var result = response?.body()?.string()
//            Log.d("test8",result)
            totalData.clear()
            var root = JSONArray(result)
            for(i in 0 until root.length()){
                var obj = root.getJSONObject(i)
                var map = HashMap<String, Any>()
                var tt:DataContents= DataContents(obj.getString("songId"),
                        obj.getString("songSubject"),
                        obj.getString("songNumber"),obj.getString("songKind"),
                        obj.getString("songEtc"),
                        obj.getString("songSite")
                )
//                Log.d("test8",tt._songSubject)
                map.put("msslData",tt)
                totalData.add(map)
            }
            runOnUiThread {
                button4.isEnabled=true
                textView3.text="Success!! 가져온 레코드 수 : ${totalData.size}"
                alert(title="확인",message = "온라인 데이터를 가져왔습니다.\n데이터 만들기를 선택하세요!") {
                    positiveButton("확인") {

                    }
                }.show()
            }
        }
    }
}