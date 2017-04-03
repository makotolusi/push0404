var reduceStatus=function Reduce(key,values){   
        return {game:values}
    }
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
        var output={replace:pvname}
        db.ClientLogBestWalkthroughCollection.mapReduce(map,r,{ out: output,query:q,sort:sor})
}
var sort={uploadDate:-1,operatorDate:-1};
//------------------------walkthrough------------------------------
  map=function Map(){ 
      emit({appId:this.appId},
      {uid:this.uid,operatorDate:this.operatorDate,uploadDate:this.uploadDate,serviceId:this.serviceId,serviceName:this.serviceName,itemType:this.itemType,operatorType:this.operatorType,
          gameCode:this.gameCode,gameType:this.gameType,gameStatus:this.gameStatus,gamePlatForm:this.gamePlatForm}) }; 
          
mr('BestWalkthroughInstalledGames',{},reduceStatus,'replace',sort);