define("jquery.autocomplete",["../theme/default/css/jquery.autocomplete.css"],function(a){return a("../theme/default/css/jquery.autocomplete.css"),function(a){return function(a){a.fn.extend({autocomplete:function(b,c){var d="string"==typeof b;return c=a.extend({},a.Autocompleter.defaults,{url:d?b:null,data:d?null:b,delay:d?a.Autocompleter.defaults.delay:10,max:c&&!c.scroll?10:150},c),c.highlight=c.highlight||function(a){return a},c.formatMatch=c.formatMatch||c.formatItem,this.each(function(){new a.Autocompleter(this,c)})},result:function(a){return this.bind("result",a)},search:function(a){return this.trigger("search",[a])},flushCache:function(){return this.trigger("flushCache")},setOptions:function(a){return this.trigger("setOptions",[a])},unautocomplete:function(){return this.trigger("unautocomplete")}}),a.Autocompleter=function(b,c){function n(){var a=l.selected();if(!a)return!1;var b=a.result;if(g=b,c.multiple){var d=p(e.val());d.length>1&&(b=d.slice(0,d.length-1).join(c.multipleSeparator)+c.multipleSeparator+b),b+=c.multipleSeparator}return e.val(b),t(),e.trigger("result",[a.data,a.value]),!0}function o(a,b){if(j==d.DEL)return l.hide(),void 0;var f=e.val();(b||f!=g)&&(g=f,f=q(f),f.length>=c.minChars?(e.addClass(c.loadingClass),c.matchCase||(f=f.toLowerCase()),v(f,u,t)):(x(),l.hide()))}function p(b){if(!b)return[""];var d=b.split(c.multipleSeparator),e=[];return a.each(d,function(b,c){a.trim(c)&&(e[b]=a.trim(c))}),e}function q(a){if(!c.multiple)return a;var b=p(a);return b[b.length-1]}function r(f,h){c.autoFill&&q(e.val()).toLowerCase()==f.toLowerCase()&&j!=d.BACKSPACE&&(e.val(e.val()+h.substring(q(g).length)),a.Autocompleter.Selection(b,g.length,g.length+h.length))}function s(){clearTimeout(f),f=setTimeout(t,200)}function t(){var d=l.visible();l.hide(),clearTimeout(f),x(),c.mustMatch&&e.search(function(a){if(!a)if(c.multiple){var b=p(e.val()).slice(0,-1);e.val(b.join(c.multipleSeparator)+(b.length?c.multipleSeparator:""))}else e.val("")}),d&&a.Autocompleter.Selection(b,b.value.length,b.value.length)}function u(a,b){b&&b.length&&i?(x(),l.display(b,a),r(a,b[0].value),l.show()):t()}function v(d,e,f){c.matchCase||(d=d.toLowerCase());var g=h.load(d);if(g&&g.length)e(d,g);else if("string"==typeof c.url&&c.url.length>0){var i={timestamp:+new Date};a.each(c.extraParams,function(a,b){i[a]="function"==typeof b?b():b}),a.ajax({mode:"abort",port:"autocomplete"+b.name,dataType:c.dataType,url:c.url,data:a.extend({q:q(d),limit:c.max},i),success:function(a){if(null==a.detail){var b=c.parse&&c.parse(a)||w(a);h.add(d,b),e(d,b)}else x()}})}else l.emptyList(),f(d)}function w(b){for(var d=[],e=b.split("\n"),f=0;e.length>f;f++){var g=a.trim(e[f]);g&&(g=g.split("|"),d[d.length]={data:g,value:g[0],result:c.formatResult&&c.formatResult(g,g[0])||g[0]})}return d}function x(){e.removeClass(c.loadingClass)}var f,j,m,d={UP:38,DOWN:40,DEL:46,TAB:9,RETURN:13,ESC:27,COMMA:188,PAGEUP:33,PAGEDOWN:34,BACKSPACE:8},e=a(b).attr("autocomplete","off").addClass(c.inputClass),g="",h=a.Autocompleter.Cache(c),i=0,k={mouseDownOnSelect:!1},l=a.Autocompleter.Select(c,b,n,k);a.browser.opera&&a(b.form).bind("submit.autocomplete",function(){return m?(m=!1,!1):void 0}),e.bind((a.browser.opera?"keypress":"keydown")+".autocomplete",function(b){switch(j=b.keyCode,b.keyCode){case d.UP:b.preventDefault(),l.visible()?l.prev():o(0,!0);break;case d.DOWN:b.preventDefault(),l.visible()?l.next():o(0,!0);break;case d.PAGEUP:b.preventDefault(),l.visible()?l.pageUp():o(0,!0);break;case d.PAGEDOWN:b.preventDefault(),l.visible()?l.pageDown():o(0,!0);break;case c.multiple&&","==a.trim(c.multipleSeparator)&&d.COMMA:case d.TAB:case d.RETURN:if(n())return b.preventDefault(),m=!0,!1;break;case d.ESC:l.hide();break;default:clearTimeout(f),f=setTimeout(o,c.delay)}}).focus(function(){i++}).blur(function(){i=0,k.mouseDownOnSelect||s()}).click(function(){i++>1&&!l.visible()&&o(0,!0)}).bind("search",function(){function c(a,c){var d;if(c&&c.length)for(var f=0;c.length>f;f++)if(c[f].result.toLowerCase()==a.toLowerCase()){d=c[f];break}"function"==typeof b?b(d):e.trigger("result",d&&[d.data,d.value])}var b=arguments.length>1?arguments[1]:null;a.each(p(e.val()),function(a,b){v(b,c,c)})}).bind("flushCache",function(){h.flush()}).bind("setOptions",function(){a.extend(c,arguments[1]),"data"in arguments[1]&&h.populate()}).bind("input",function(){o(0,!0)}).bind("unautocomplete",function(){l.unbind(),e.unbind(),a(b.form).unbind(".autocomplete").bind("input",function(){o(0,!0)})})},a.Autocompleter.defaults={inputClass:"ac_input",resultsClass:"ac_results",loadingClass:"ac_loading",minChars:1,delay:400,matchCase:!1,matchSubset:!0,matchContains:!1,cacheLength:10,max:100,mustMatch:!1,extraParams:{},selectFirst:!0,formatItem:function(a){return a[0]},formatMatch:null,autoFill:!1,width:0,multiple:!1,multipleSeparator:", ",highlight:function(a,b){return a.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)("+b.replace(/([\^\$\(\)\[\]\{\}\*\.\+\?\|\\])/gi,"\\$1")+")(?![^<>]*>)(?![^&;]+;)","gi"),"<strong>$1</strong>")},scroll:!0,scrollHeight:180},a.Autocompleter.Cache=function(b){function e(a,c){b.matchCase||(a=a.toLowerCase());var d=a.indexOf(c);return-1==d?!1:0==d||b.matchContains}function f(a,e){d>b.cacheLength&&h(),c[a]||d++,c[a]=e}function g(){if(!b.data)return!1;var c={},d=0;b.url||(b.cacheLength=1),c[""]=[];for(var e=0,g=b.data.length;g>e;e++){var h=b.data[e];h="string"==typeof h?[h]:h;var i=b.formatMatch(h,e+1,b.data.length);if(i!==!1){var j=i.charAt(0).toLowerCase();c[j]||(c[j]=[]);var k={value:i,data:h,result:b.formatResult&&b.formatResult(h)||i};c[j].push(k),d++<b.max&&c[""].push(k)}}a.each(c,function(a,c){b.cacheLength++,f(a,c)})}function h(){c={},d=0}var c={},d=0;return setTimeout(g,25),{flush:h,add:f,populate:g,load:function(f){if(!b.cacheLength||!d)return null;if(!b.url&&b.matchContains){var g=[];for(var h in c)if(h.length>0){var i=c[h];a.each(i,function(a,b){e(b.value,f)&&g.push(b)})}return g}if(c[f])return c[f];if(b.matchSubset)for(var j=f.length-1;j>=b.minChars;j--){var i=c[f.substr(0,j)];if(i){var g=[];return a.each(i,function(a,b){e(b.value,f)&&(g[g.length]=b)}),g}}return null}}},a.Autocompleter.Select=function(b,c,d,e){function n(){k&&(l=a("<div/>").hide().addClass(b.resultsClass).css("position","absolute").appendTo(document.body),m=a("<ul/>").appendTo(l).mouseover(function(b){o(b).nodeName&&"LI"==o(b).nodeName.toUpperCase()&&(h=a("li",m).removeClass(f.ACTIVE).index(o(b)),a(o(b)).addClass(f.ACTIVE))}).click(function(b){return a(o(b)).addClass(f.ACTIVE),d(),c.focus(),!1}).mousedown(function(){e.mouseDownOnSelect=!0}).mouseup(function(){e.mouseDownOnSelect=!1}),b.width>0&&l.css("width",b.width),k=!1)}function o(a){for(var b=a.target;b&&"LI"!=b.tagName;)b=b.parentNode;return b?b:[]}function p(a){g.slice(h,h+1).removeClass(f.ACTIVE),q(a);var c=g.slice(h,h+1).addClass(f.ACTIVE);if(b.scroll){var d=0;g.slice(0,h).each(function(){d+=this.offsetHeight}),d+c[0].offsetHeight-m.scrollTop()>m[0].clientHeight?m.scrollTop(d+c[0].offsetHeight-m.innerHeight()):m.scrollTop()>d&&m.scrollTop(d)}}function q(a){h+=a,0>h?h=g.size()-1:h>=g.size()&&(h=0)}function r(a){return b.max&&a>b.max?b.max:a}function s(){m.empty();for(var c=r(i.length),d=0;c>d;d++)if(i[d]){var e=b.formatItem(i[d].data,d+1,c,i[d].value,j);if(e!==!1){var k=a("<li/>").html(b.highlight(e,j)).addClass(0==d%2?"ac_even":"ac_odd").appendTo(m)[0];a.data(k,"ac_data",i[d])}}g=m.find("li"),b.selectFirst&&(g.slice(0,1).addClass(f.ACTIVE),h=0),a.fn.bgiframe&&m.bgiframe()}var g,i,l,m,f={ACTIVE:"ac_over"},h=-1,j="",k=!0;return{display:function(a,b){n(),i=a,j=b,s()},next:function(){p(1)},prev:function(){p(-1)},pageUp:function(){0!=h&&0>h-8?p(-h):p(-8)},pageDown:function(){h!=g.size()-1&&h+8>g.size()?p(g.size()-1-h):p(8)},hide:function(){l&&l.hide(),g&&g.removeClass(f.ACTIVE),h=-1},visible:function(){return l&&l.is(":visible")},current:function(){return this.visible()&&(g.filter("."+f.ACTIVE)[0]||b.selectFirst&&g[0])},
show:function(){var d=a(c).offset();if(l.css({width:"string"==typeof b.width||b.width>0?b.width:a(c).width(),top:d.top+c.offsetHeight,left:d.left}).show(),b.scroll&&(m.scrollTop(0),m.css({maxHeight:b.scrollHeight,overflow:"auto"}),a.browser.msie&&"undefined"==typeof document.body.style.maxHeight)){var e=0;g.each(function(){e+=this.offsetHeight});var f=e>b.scrollHeight;m.css("height",f?b.scrollHeight:e),f||g.width(m.width()-parseInt(g.css("padding-left"))-parseInt(g.css("padding-right")))}},selected:function(){var b=g&&g.filter("."+f.ACTIVE).removeClass(f.ACTIVE);return b&&b.length&&a.data(b[0],"ac_data")},emptyList:function(){m&&m.empty()},unbind:function(){l&&l.remove()}}},a.Autocompleter.Selection=function(a,b,c){if(a.createTextRange){var d=a.createTextRange();d.collapse(!0),d.moveStart("character",b),d.moveEnd("character",c),d.select()}else a.setSelectionRange?a.setSelectionRange(b,c):a.selectionStart&&(a.selectionStart=b,a.selectionEnd=c);a.focus()}}(a),a.noConflict(!0)}});