db.ClientLogCollection.find().forEach(function(x){
    x.serviceName='';
    db.ClientLogCollection.save(x)
    })