
var refresher = {
	info: {
		"pullUpLable": "上拉加载更多",
		"pullingUpLable": "释放加载更多",
		"loadingLable": "加载中"
	},
	init: function(parameter) {
		var wrapper = document.getElementById(parameter.id);
		var cont_box = document.getElementById('cont_box');
		var div = document.createElement("div");
		div.className = "scroller";
		cont_box.appendChild(div);
		var scroller = wrapper.querySelector(".scroller");
		//var list = wrapper.querySelector("#" + parameter.id + " ul");
		//scroller.insertBefore(list, scroller.childNodes[0]);
		var pullUp = document.createElement("div");
		pullUp.className = "pullUp";
		var loader = document.createElement("div");
		loader.className = "pullUpIcon";
		pullUp.appendChild(loader);
		var pullUpLabel = document.createElement("div");
		pullUpLabel.className = "pullUpLabel";
		var content = document.createTextNode(refresher.info.pullUpLable);
		pullUpLabel.appendChild(content);
		pullUp.appendChild(pullUpLabel);
		scroller.appendChild(pullUp);
		var pullDownEle = '';
		var pullDownOffset = 0;
		var pullUpEle = wrapper.querySelector(".pullUp");
		var pullUpOffset = pullUpEle.offsetHeight;
		this.scrollIt(parameter, pullUpEle, pullUpOffset);
	},
	scrollIt: function(parameter, pullUpEle, pullUpOffset) {
		eval(
			parameter.id + "= new iScroll(\
			parameter.id,\
			{\
				useTransition: true,\
				vScrollbar: false,\
				onRefresh: function () {\
				  refresher.onRelease(pullUpEle);\
				},\
				onScrollMove: function () {\
				    refresher.onScrolling(this,pullUpEle,pullUpOffset);\
				},\
				onScrollEnd: function () {\
		        	refresher.onScrollEnd(pullUpEle,parameter.pullUpAction);\
				}\
			})"
		);
	},
	onScrolling: function(e, pullUpEle, pullUpOffset) {
		if (e.scrollerH < e.wrapperH &&e.y>e.maxScrollY-pullUpOffset&&pullUpEle.className.match("flip") || e.scrollerH > e.wrapperH &&e.y>e.maxScrollY-pullUpOffset&&pullUpEle.className.match("flip") ) {
			pullUpEle.classList.remove("flip");
			pullUpEle.querySelector('.pullUpLabel').innerHTML = refresher.info.pullUpLable;
		}
		if (e.scrollerH < e.wrapperH && e.y < (e.minScrollY - pullUpOffset) &&!pullUpEle.className.match('loading')|| e.scrollerH > e.wrapperH && e.y < (e.maxScrollY - pullUpOffset)&&!pullUpEle.className.match('loading')) {
			pullUpEle.classList.add("flip");
			pullUpEle.querySelector('.pullUpLabel').innerHTML = refresher.info.pullingUpLable;
		}
	},
	onRelease: function( pullUpEle) {
		if (pullUpEle.className.match('loading')) {
			pullUpEle.classList.toggle("loading");
			pullUpEle.querySelector('.pullUpLabel').innerHTML = refresher.info.pullUpLable;
		}
	},
	onScrollEnd: function(pullUpEle, pullUpAction) {
		if (pullUpEle.className.match('flip')&&!pullUpEle.className.match('loading')) {
			pullUpEle.classList.add("loading");
			pullUpEle.classList.remove("flip");
			pullUpEle.querySelector('.pullUpLabel').innerHTML = refresher.info.loadingLable;
			if (pullUpAction) pullUpAction();
		}
	}
}