var reduceStatus=function Reduce(key,values){   
    var total=0; 
    for(var i=0;i<values.length;i++){ 
        total+=values[i].pv; 
            }     
              var sta=values[values.length-1].state;
        return {pv:total,serviceName:values[0].serviceName,itemType:values[0].itemType,operatorDate:values[0].operatorDate,uploadDate:values[0].uploadDate,
            gameCode:values[0].gameCode,gameType:values[0].gameType,gameStatus:values[0].gameStatus,gamePlatForm:values[0].gamePlatForm,keyWord:values[0].keyWord,keyWord2:values[0].keyWord2,
            state:sta==undefined?0:sta}; }
var map;

function mr(pvname,q,r,out,sor){
     
    var oldTime=db.StatisticJobLastUpdateTime.findOne({'statisicJobName':pvname});
    print(' old time is ');
    printjson(oldTime);
    if(oldTime!=null)
    {
     
        q['uploadDate']={$gt:oldTime['lastUpdateTime']}
        db.StatisticJobLastUpdateTime.remove({'statisicJobName':pvname})
    }
        
    db.StatisticJobLastUpdateTime.insert({'statisicJobName':pvname,'lastUpdateTime':new Date()});

        db.ClientLogCollection.mapReduce(map,r,{ out: {reduce:pvname},query:q,sort:sor})
   
  
}

var sort={uploadDate:-1,operatorDate:-1};
//------------------------173App------------------------------
  map=function Map(){ 
     
      emit({appId:(this.appId==undefined?-1:this.appId),uid:this.uid,serviceId:this.serviceId,operatorType:this.operatorType},
      {pv:1,operatorDate:this.operatorDate,uploadDate:this.uploadDate,serviceName:this.serviceName,itemType:this.itemType,
          gameCode:this.gameCode,gameType:this.gameType,gameStatus:this.gameStatus,gamePlatForm:this.gamePlatForm,keyWord:this.keyWord,keyWord2:this.keyWord2,state:(this.state==undefined?0:this.state)}); }      
mr('UserItemOperatePv',{} ,reduceStatus,'reduce',sort);