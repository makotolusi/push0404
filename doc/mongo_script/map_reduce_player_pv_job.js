var startTime=new Date();
print("start date "+startTime);
//var init
var is1={type:'pv'}
var is2={type:'av'}
//获取昨天的日期start
var dd = new Date(new Date().valueOf() - 24*3600000);//获取昨天的日期
var y = dd.getFullYear();
var m = dd.getMonth()+1;//获取当前月份的日期
var d = dd.getDate();
if(m<10) m="0"+m;
if(d<10) d="0"+d;
var yestoday= y+"-"+m+"-"+d;
//var yestoday="2014-06-27";
//end
var reduce=function(key,values){
    result = {uv:0,pv:0,time:0,appid:'',statdate:''};
    for(var i in values) { 
        result.uv += values[i].uv;
        result.pv += values[i].pv;
        result.time += values[i].time;
        result.appid = values[i].appid;
        result.statdate = values[i].statdate;
    }
    result.time = result.time.toFixed(1);
    //result.vv =result.pv/result.vv;
    return result;}
var map;
function mr(pvname,q){
    q['lastUpdate']=new RegExp(yestoday);//模糊查询
    
    //start
    db.PlayerLog.mapReduce(
        map,
        reduce,
        { out: {replace:pvname},
          query:q
        }
    )
}

print("SearchPv start")
map=function(){
    var dd = new Date(new Date().valueOf() - 24*3600000);//获取昨天的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if(m<10) m="0"+m;
    if(d<10) d="0"+d;
    var yestoday= y+"-"+m+"-"+d;
    //var yestoday="2014-06-27";
    emit({appid:this.appid,uid:this.uid},{uv:1,pv:1,time:NumberLong(this.playtime)/1000,appid:this.appid,statdate:yestoday});
}
mr('tmp_pv_uid',is1);

var map1=function(){emit({appid:this.value.appid},{uv:1,pv:this.value.pv,time:this.value.time,appid:this.value.appid,statdate:this.value.statdate});}
db.tmp_pv_uid.mapReduce(
        map1,
        reduce,
        { out: {replace:'tmp_pv_uid1'}}
    )
//delete old data
var dd = new Date(new Date().valueOf() - 24*3600000);//获取昨天的日期
var y = dd.getFullYear();
var m = dd.getMonth()+1;//获取当前月份的日期
var d = dd.getDate();
if(m<10) m="0"+m;
if(d<10) d="0"+d;
var yestoday= y+"-"+m+"-"+d;
//var yestoday="2014-06-27";
db.PlayerPvList.remove({statdate:yestoday})
//insert
var c=db.tmp_pv_uid1.find()

c.forEach(function(obj){
    //print(tojson(obj._id ))
    //var doc=c.next()._id 
    db.PlayerPvList.insert(obj.value)
})
print("SearchPv end")

print("SearchAv start")
map=function(){
   var dd = new Date(new Date().valueOf() - 24*3600000);//获取昨天的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if(m<10) m="0"+m;
    if(d<10) d="0"+d;
    var yestoday= y+"-"+m+"-"+d;
    //var yestoday="2014-06-27";
   emit({appid:this.appid,uid:this.uid},{uv:1,pv:1,time:NumberLong(this.playtime)/1000,appid:this.appid,statdate:yestoday});}
mr('tmp_av_uid',is2);

var map1=function(){emit({appid:this.value.appid},{uv:1,pv:this.value.pv,time:this.value.time,appid:this.value.appid,statdate:this.value.statdate});}
db.tmp_av_uid.mapReduce(
        map1,
        reduce,
        { out: {replace:'tmp_av_uid1'}}
    )
//delete old data
var dd = new Date(new Date().valueOf() - 24*3600000);//获取昨天的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if(m<10) m="0"+m;
    if(d<10) d="0"+d;
    var yestoday= y+"-"+m+"-"+d;
    //var yestoday="2014-06-27";
db.PlayerAvList.remove({statdate:yestoday})
//insert
var c=db.tmp_av_uid1.find()
c.forEach(function(obj){
    //print(tojson(obj._id ))
    db.PlayerAvList.insert(obj.value)
})

print("SearchAv end")

print("delete tmp collections start")
db.tmp_pv_uid.drop();
db.tmp_pv_uid1.drop();
db.tmp_av_uid.drop();
db.tmp_av_uid1.drop();
print("delete tmp collections end")

var endTime =new Date();
print("excute time is "+(endTime-startTime)/1000)