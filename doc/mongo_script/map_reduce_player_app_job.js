/*
1小时执行一次，整点执行，查询上一小时的数据写入新表
*/
var startTime=new Date();
print("start date "+startTime);
//var init
var dd=new Date(new Date().valueOf() - 3600000);
var y = dd.getFullYear();
var m = dd.getMonth()+1;//获取当前月份的日期
var d = dd.getDate();
var h = dd.getHours();
if(m<10) m="0"+m;
if(d<10) d="0"+d;
if(h<10) h="0"+h;
var time= y+"-"+m+"-"+d +" "+h;
//var time="2014-06-27 16";
var reduce=function(key,values){return Array.sum(values);}
var map;
function mr(pvname,q){
    //start
    q['lastUpdate']=new RegExp(time);//模糊查询
    db.PlayerLog.mapReduce(
        map,
        reduce,
        { out: {replace:pvname},
          query:q
        }
    )
}

print("SearchKeyWordPv start")
//SearchNewAppid start
map=function(){ emit({appid:this.appid,appName:this.appname,advState:"1",localState:"0"},1);}
mr('tmp_Apps',{});
//insert
var c=db.tmp_Apps.find()
c.forEach(function(obj){
    //print(tojson(obj._id ))
    //var doc=c.next()._id 
    db.PlayerApps.insert(obj._id)
})

print("SearchKeyWordPv end")

print("delete tmp collections start")
db.tmp_Apps.drop();
print("delete tmp collections end")

var endTime =new Date();
print("excute time is "+(endTime-startTime)/1000)