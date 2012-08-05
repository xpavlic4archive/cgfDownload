db.names2.group({key: { club:true},reduce: function(obj,prev) { prev.csum += 1; },initial: { csum: 0 }});

db.names2.find().sort({number: -1})
