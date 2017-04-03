//var init
var is5={itemType:'5'}
var ne5={itemType:{$ne:'5'}}

var reduce=function Reduce(key,values){ 
    var total=0; 
    var state=0; 
    //var serviceName='';
    for(var i=0;i<values.length;i++){ 
        total+=values[i].pv; 
       // if( values[i].serviceName!='') serviceName=values[i].serviceName;	
        if( values[i].state==1) state=1; if(values[i].state==2) state=2;
            }
       
        return {pv:total,serviceName:values[0].serviceName,itemType:values[0].itemType,state:state}; }
var map;
function mr(pvname,q,r){
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
        { out: {reduce:pvname},
          query:q,
          sort:{uploadDate:-1}
        }
    )
    
}



      
print("ItemPv start")
//ItemPv start
  map=function Map(){ var serviceName=''; if(this.serviceName!=null) serviceName=this.serviceName; 
      emit({serviceId:this.serviceId},{pv:1,state:0,serviceName:serviceName,itemType:this.itemType}); }    
mr('ItemPv',ne5,reduce);
print("ItemPv end")
