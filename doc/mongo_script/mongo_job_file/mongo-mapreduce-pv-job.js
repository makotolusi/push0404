var startTime=new Date();
print("start date "+startTime);
//var init
var is5={itemType:'5'}
var ne5={itemType:{$ne:'5'}}
var reduce=function(key,values){return Array.sum(values);}
var map;
function mr(pvname,q){
   var oldTimeObj=db.StatisticJobLastUpdateTime.find({"statisicJobName" : 'UserItemOperatePv'}).sort({lastUpdateTime : -1}).limit(1)
   var oldTime;
    if(oldTimeObj.size()==0)
    	 printjson('first time excute ');
    else{
    	oldTimeObj.forEach(function(x) {
            oldTime=x.lastUpdateTime;
            printjson(oldTime)
    
        })
 
     
        q['uploadDate']={$gt:oldTime}
        db.StatisticJobLastUpdateTime.remove({'statisicJobName':pvname})
    }
        
    //time flag
    db.StatisticJobLastUpdateTime.insert({'statisicJobName':pvname,'lastUpdateTime':new Date()});
    //start
    db.ClientLogCollection.mapReduce(
        map,
        reduce,
        { out: {reduce:pvname},
          query:q
        }
    )
    
}

print("SearchKeyWordPv start")
//SearchKeyWordPv start
map=function(){ emit({keyWord:this.keyWord},1);}
mr('SearchKeyWordPv',is5);
print("SearchKeyWordPv end")

print("UserSearchKeyWordPv start")
//UserSearchKeyWordPv start
map=function(){ emit({uid:this.uid,keyWord:this.keyWord},1);}
mr('UserSearchKeyWordPv',is5);
print("UserSearchKeyWordPv end")

print("ItemOperatePv start")
//ItemOperatePv start
map=function(){emit({serviceId:this.serviceId,itemType:this.itemType,operatorType:this.operatorType},1);}
mr('ItemOperatePv',ne5);
print("ItemOperatePv end")

print("ItemPv start")
//ItemPv start
map=function(){emit({serviceId:this.serviceId,itemType:this.itemType},1);}
mr('ItemPv',ne5);
print("ItemPv end")

print("UserItemOperatePv start")//UserItemOperatePv start
map=function(){ emit({uid:this.uid,serviceId:this.serviceId,itemType:this.itemType,operatorType:this.operatorType},1);}
mr('UserItemOperatePv',ne5);
print("UserItemOperatePv end")


var endTime =new Date();
print("excute time is "+(endTime-startTime)/1000)