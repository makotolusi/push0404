
var is5={itemType:'5'}
var ne5={itemType:{$ne:'5'}}

var colSub={operatorType:{$in:['5','6']}}
var favorate={operatorType:{$in:['3','4']}}
var top={operatorType:{$in:['8','9']}}

var reduce=function Reduce(key,values){ 
    var total=0; 
    var state=0; 
      var serviceName='';
    var flag=false;
    for(var i=0;i<values.length;i++){ 
        total+=values[i].pv; 
     
         if(flag==false)
        if( values[i].serviceName!='') {serviceName=values[i].serviceName; flag=true;	}	
        if( values[i].state==1) state=1; if(values[i].state==2) state=2;
            }
       
        return {pv:total,serviceName:serviceName,itemType:values[0].itemType,state:state}; }
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
        var output={};
    if(out=='merge')
        output={merge:pvname};
    else
        output={reduce:pvname}
   
        if(sor==undefined)
            sor={};

    db.ClientLogCollection.mapReduce(
        map,
        r,
        { out: output,
          query:q,
          sort:sor
        }
    )
    
}



      
print("SearchKeyWordPv start")

  map=function Map(){

      emit({keyWord:this.keyWord},{pv:1,state:0,serviceName:this.serviceName,itemType:'5'}); }  
mr('SearchKeyWordPv',is5,reduce,'reduce',{uploadDate:-1});
print("SearchKeyWordPv end")
      
print("UserSearchKeyWordPv start")

  map=function Map(){
   
      emit({uid:this.uid,keyWord:this.keyWord},{pv:1,state:0,serviceName:this.serviceName,itemType:'5'}); }  
mr('UserSearchKeyWordPv',is5,reduce,'reduce',{uploadDate:-1});
print("UserSearchKeyWordPv end")

print("ItemPv start")

  map=function Map(){ var serviceName=''; if(this.serviceName!=null) serviceName=this.serviceName; 
      emit({serviceId:this.serviceId},{pv:1,state:0,serviceName:serviceName,itemType:this.itemType}); }    
mr('ItemPv',ne5,reduce,'reduce',{uploadDate:-1});
print("ItemPv end")
      
print("ItemOperatePv start")

  map=function Map(){ var serviceName=''; if(this.serviceName!=null) serviceName=this.serviceName; 
      emit({serviceId:this.serviceId,operatorType:this.operatorType,},{pv:1,state:0,serviceName:serviceName,itemType:this.itemType}); }      
mr('ItemOperatePv',ne5,reduce,'reduce',{uploadDate:-1});
print("ItemOperatePv end")

print("UserItemOperatePv start")

  map=function Map(){ 
      emit({uid:this.uid,serviceId:this.serviceId,operatorType:this.operatorType},{pv:1,state:0,serviceName:this.serviceName,itemType:this.itemType}); }      
mr('UserItemOperatePv',ne5,reduce,'reduce',{uploadDate:-1});
print("UserItemOperatePv end")


      
print("UserSearchContentPv start")

  map=function Map(){
      emit({uid:this.uid,serviceId:this.serviceId},{pv:1,state:0,serviceName:this.serviceName,itemType:this.itemType}); }      
mr('UserSearchContentPv',{otherWay:5},reduce,'reduce',{uploadDate:-1});
print("UserSearchContentPv end")
    
print("SearchContentPv start")

  map=function Map(){
      emit({serviceId:this.serviceId},{pv:1,state:0,serviceName:this.serviceName,itemType:this.itemType}); }      
mr('SearchContentPv',{otherWay:5},reduce,'reduce',{uploadDate:-1});
print("SearchContentPv end")


      
print("UserSubScribe start")

  map=function Map(){ 
      emit({uid:this.uid,serviceId:this.serviceId},{itemType:this.itemType,operatorType:this.operatorType,operatorDate:this.operatorDate}); }    
    
var r1=function Reduce(key,values){ 
    return {itemType:values[0].itemType,operatorType:values[0].operatorType,operatorDate:values[0].operatorDate}; }
  
mr('UserSubScribe',colSub,r1,'merge',{operatorDate:-1});
print("UserSubScribe end")


print("UserFavorate start")  
mr('UserFavorate',favorate,r1,'merge',{operatorDate:-1});
print("UserFavorate end")


print("UserTop start")  
mr('UserTop',top,r1,'merge',{operatorDate:-1});
print("UserTop end")

