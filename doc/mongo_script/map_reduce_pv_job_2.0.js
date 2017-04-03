
var is5={itemType:'5'}
var ne5={itemType:{$ne:'5'}}

function initParam(){
 is5={itemType:'5'}
 ne5={itemType:{$ne:'5'}}  
}
var reduceStatus=function Reduce(key,values){ 
    //printjson(values)
    var total=0; 
    for(var i=0;i<values.length;i++){ 
        total+=values[i].pv; 
            }
              var sta=0
         if(values[values.length-1].state!=undefined)
           sta=values[values.length-1].state;
        return {pv:total,serviceName:values[0].serviceName,itemType:values[0].itemType,operatorDate:values[0].operatorDate,uploadDate:values[0].uploadDate,
            gameCode:values[0].gameCode,gameType:values[0].gameType,gameStatus:values[0].gameStatus,gamePlatForm:values[0].gamePlatForm,keyWord:values[0].keyWord,keyWord2:values[0].keyWord2}; }
        
var map;
function mr(pvname,q,r,out,sor){
    print(pvname)
    initParam();
    var field='';
       if(pvname=='UserItemOperatePv')
            field='uploadDate';
       else
            field='value.uploadDate';
    var oldTime=db.StatisticJobLastUpdateTime.findOne({'statisicJobName':pvname});
    print(' old time is ');
    printjson(oldTime);
    if(oldTime!=null)
    {
     
        q[field]={$gt:oldTime['lastUpdateTime']}
        db.StatisticJobLastUpdateTime.remove({'statisicJobName':pvname})
    }
        
    db.StatisticJobLastUpdateTime.insert({'statisicJobName':pvname,'lastUpdateTime':new Date()});
        var output={};
    if(out=='merge')
        output={merge:pvname};
    else
        output={reduce:pvname}
            
        print(q)
    if(pvname=='UserItemOperatePv'||pvname=='UserSearchKeyWordPv')
        db.ClientLogCollection.mapReduce(map,r,{ out: output,query:q,sort:sor})
    if(pvname=='ItemOperatePv')
        db.UserItemOperatePv.mapReduce(map,r,{ out: output,query:q,sort:sor}) 
        if(pvname=='UserGamePv')
        db.UserItemOperatePv.mapReduce(map,r,{ out: output,query:q,sort:sor}) 
    if(pvname=='ItemPv')
        db.ItemOperatePv.mapReduce(map,r,{ out: output,query:q,sort:sor})    
     if(pvname=='SearchKeyWordPv')
        db.UserSearchKeyWordPv.mapReduce(map,r,{ out: output,query:q,sort:sor})    
  
}


var sort={uploadDate:-1,operatorDate:-1};
  /**  
    **/

  map=function Map(){ 
         var otherWay=0;
      if(this.otherWay!=undefined)
          otherWay=this.otherWay
      emit({uid:this.uid,serviceId:this.serviceId,operatorType:this.operatorType,otherWay:otherWay},
      {pv:1,operatorDate:this.operatorDate,uploadDate:this.uploadDate,serviceName:this.serviceName,itemType:this.itemType,
          gameCode:this.gameCode,gameType:this.gameType,gameStatus:this.gameStatus,gamePlatForm:this.gamePlatForm,keyWord:this.keyWord,keyWord2:this.keyWord2}); }      
mr('UserItemOperatePv',{itemType:{$ne:'5'}} ,reduceStatus,'reduce',sort);


 
  map=function Map(){ 
      emit({uid:this._id.uid,gameCode:this.value.gameCode},
       {pv:this.value.pv,operatorDate:this.value.operatorDate,serviceName:this.value.serviceName,uploadDate:this.value.uploadDate,itemType:this.value.itemType,
          gameCode:this.value.gameCode,gameType:this.value.gameType,gameStatus:this.value.gameStatus,gamePlatForm:this.value.gamePlatForm,keyWord:this.value.keyWord,keyWord2:this.value.keyWord2}); }    
mr('UserGamePv',{'value.gameCode':{ $exists: true,'$ne':''}},reduceStatus,'merge',{'value.uploadDate':-1,'value.operatorDate':-1});



  map=function Map(){ 
      emit({serviceId:this._id.serviceId,operatorType:this._id.operatorType,},
       {pv:this.value.pv,operatorDate:this.value.operatorDate,uploadDate:this.value.uploadDate,serviceName:this.value.serviceName,itemType:this.value.itemType,
          gameCode:this.value.gameCode,gameType:this.value.gameType,gameStatus:this.value.gameStatus,gamePlatForm:this.value.gamePlatForm,keyWord:this.value.keyWord,keyWord2:this.value.keyWord2}); }    
mr('ItemOperatePv',{},reduceStatus,'merge',{'value.uploadDate':-1,'value.operatorDate':-1});

  map=function Map(){ 
      emit({serviceId:this._id.serviceId},
    {pv:this.value.pv,operatorDate:this.value.operatorDate,uploadDate:this.value.uploadDate,serviceName:this.value.serviceName,itemType:this.value.itemType,
          gameCode:this.value.gameCode,gameType:this.value.gameType,gameStatus:this.value.gameStatus,gamePlatForm:this.value.gamePlatForm,keyWord:this.value.keyWord,keyWord2:this.value.keyWord2}); }       
mr('ItemPv',{},reduceStatus,'merge',{'value.uploadDate':-1,'value.operatorDate':-1});


 
//------------------------search------------------------------

var reduce=function Reduce(key,values){ 
    //printjson(values)
    var total=0; 
    for(var i=0;i<values.length;i++){ 
        total+=values[i].pv; 
            }
        return {pv:total,serviceName:values[0].serviceName,itemType:values[0].itemType,operatorDate:values[0].operatorDate,uploadDate:values[0].uploadDate}; }
            
print("UserSearchKeyWordPv start")

  map=function Map(){
   
      emit({uid:this.uid,keyWord:this.keyWord},{pv:1,operatorDate:this.operatorDate,uploadDate:this.uploadDate,serviceName:this.serviceName,itemType:'5'}); }  
mr('UserSearchKeyWordPv',{itemType:'5'},reduce,'reduce',sort);
print("UserSearchKeyWordPv end")
      
print("SearchKeyWordPv start")

  map=function Map(){

      emit({keyWord:this._id.keyWord},{pv:this.value.pv,operatorDate:this.value.operatorDate,uploadDate:this.value.uploadDate,serviceName:this.value.serviceName,itemType:'5'}); }  
mr('SearchKeyWordPv',{},reduce,'reduce',{});
print("SearchKeyWordPv end")
      


  /**    **/
      

