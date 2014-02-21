/** * 公共常量 */define(function(require, exports, module) {	var $=require("$");	var map = {};	var Constants = {		codeName : {			items:null,			getName : function(value) {				var name = "";				if(this.items){					for(var i=0;i<this.items.length;i++){						var item = this.items[i];						if(value == item.code){							name =  item.name;							break;						}					}				}				return name;			}		},		get:function(key){			if(!map[key]){				var url=G.API.CODENAME;				$.ajax({					url : url,					type : 'GET',					async : false,					data:{						"key":key,						"param":""					},					success : function(data) {						if (!data.message) {							map[key] = data.items;						} else {							Log.error(data.message);						}					}				});			}			var codeName = this.codeName;			codeName.items = map[key];			return codeName;		}	};	module.exports = Constants;});