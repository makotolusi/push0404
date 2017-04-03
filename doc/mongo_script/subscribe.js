
//var init
var is5={itemType:'5'}
var ne5={itemType:{$ne:'5'}}
var colSub={operatorType:{$in:['5','6']}}
var favorate={operatorType:{$in:['3','4']}}
var top={operatorType:{$in:['8','9']}}
var map;
function mr(pvname,q,r,sor){
    var oldTime=db.StatisticJobLastUpdateTime.findOne({'statisicJobName':pvname});
    print(' old time is ');
    printjson(oldTime);
    if(oldTime!=null)
    {
     
        q['uploadDate']={$gt:oldTime['lastUpdateTime']}
        db.StatisticJobLastUpdateTime.remove({'statisicJobName':pvname})
    }
        
    //time flag
    db.StatisticJobLastUpdateTime.insert({'statisicJobName':pvname,'lastUpdateTime':new Date()});
    //start
    db.ClientLogCollection.mapReduce(
        map,
        r,
        { out: {merge:pvname},
          query:q,
          sort:sor
        }
    )
    
}



print("UserSubScribe start")
//UserItemOperatePv start
  map=function Map(){ 
      emit({uid:this.uid,serviceId:this.serviceId},{itemType:this.itemType,operatorType:this.operatorType,operatorDate:this.operatorDate}); }    
    
var r1=function Reduce(key,values){ 
    return {itemType:values[0].itemType,operatorType:values[0].operatorType,operatorDate:values[0].operatorDate}; }
  
mr('UserSubScribe',colSub,r1,{operatorDate:-1});
print("UserSubScribe end")


print("UserFavorate start")  
mr('UserFavorate',favorate,r1,{operatorDate:-1});
print("UserFavorate end")


print("UserTop start")  
mr('UserTop',top,r1,{operatorDate:-1});
print("UserTop end")