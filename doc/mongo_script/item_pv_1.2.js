
var is5={itemType:'5'}
var ne5={itemType:{$ne:'5'}}

var colSub={operatorType:{$in:['5','6']}}
var favorate={operatorType:{$in:['3','4']}}
var top={operatorType:{$in:['8','9']}}

var reduce=function Reduce(key,values){  var total=0;  for(var i=0;i<values.length;i++){  total+=values[i].pv; 	} return {pv:total,serviceName:'',itemType:values[0].itemType}; }
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




print("ItemPv start")

  map=function Map(){ emit({serviceId:this.serviceId},{pv:1,serviceName:'',itemType:this.itemType}); }    
mr('ItemPv',ne5,reduce,'reduce');
print("ItemPv end")

