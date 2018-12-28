function jBox(type, options) {
	this.options = {
		id: null,
		width: 'auto',
		height: 'auto',
		attach: null,
		trigger: 'click',
		preventDefault: false,
		title: null,
		content: null,
		getTitle: null,
		getContent: null,
		ajax: null,
		data: '',
		reload: false,
		target: null,
		position: {
			x: 'center',
			y: 'center'
		},
		outside: null,
		offset: 0,
		attributes: {
			x: 'left',
			y: 'top'
		},
		adjustPosition: false,
		adjustTracker: false,
		adjustDistance: 5,
		fixed: false,
		reposition: false,
		pointer: false,
		fade: 180,
		animation: null,
		theme: 'Default',
		addClass: '',
		overlay: false,
		zIndex: 10000,
		delayOpen: 0,
		delayClose: 0,
		closeOnEsc: false,
		closeOnClick: false,
		closeOnMouseleave: false,
		closeButton: false,
		constructOnInit: false,
		blockScroll: false,
		appendTo: jQuery('body'),
		draggable: null,
		onInit: function() {},
		onCreated: function() {},
		onOpen: function() {},
		onClose: function() {},
		onAjax: function() {},
		onAjaxComplete: function() {},
		autoClose: 1000,
		color: null,
		stack: true,
		audio: false,
		volume: 100,
		footer:false,
		footerContent:$("<button>"),
		confrimFunc:null

	};
	this.defaultOptions = {
		'Tooltip': {
			getContent: 'title',
			trigger: 'mouseenter',
			position: {
				x: 'center',
				y: 'top'
			},
			outside: 'y',
			pointer: true,
			adjustPosition: true,
			reposition: true
		},
		'Mouse': {
			target: 'mouse',
			position: {
				x: 'right',
				y: 'bottom'
			},
			offset: 15,
			trigger: 'mouseenter',
			adjustPosition: 'flip'
		},
		'Modal': {
			target: jQuery(window),
			fixed: true,
			blockScroll: true,
			closeOnEsc: true,
			closeOnClick: 'body',
			closeButton: 'title',
			overlay: true,
			animation: 'zoomOut'
		},
		'Notice': {
			target: jQuery(window),
			fixed: true,
			position: {
				x: 'center',
				y: 'center'
			},
			attributes: {
				x: 'right',
				y: 'top'
			},
			animation: 'zoomIn',
			closeOnClick: 'box',
			_onInit: function() {
				this.open();
				this.options.delayClose = this.options.autoClose;
				this.options.delayClose && this.close()
			}.bind(this),
			_onCreated: function() {
				this.options.color && this.wrapper.addClass('jBox-Notice-color jBox-Notice-' + this.options.color)
			},
			_onOpen: function() {
				jQuery.each(jQuery('.jBox-Notice'), function(index, el) {
					el = jQuery(el);
					if (el.attr('id') == this.id || el.css(this.options.attributes.y) == 'auto' || el.css(this.options.attributes.x) == 'auto') return;
					if (!this.options.stack) {
						el.data('jBox').close({
							ignoreDelay: true
						});
						return
					}
					el.css('margin-' + this.options.attributes.y, parseInt(el.css('margin-' + this.options.attributes.y)) + this.dimensions.y + 10)
				}.bind(this));
				if (this.options.audio && !this.IE8) {
					this.audio = jQuery('<audio/>');
					jQuery('<source/>', {
						src: this.options.audio + '.mp3'
					}).appendTo(this.audio);
					jQuery('<source/>', {
						src: this.options.audio + '.ogg'
					}).appendTo(this.audio);
					this.audio[0].volume = Math.min((this.options.volume / 100), 1);
					this.audio[0].play()
				}
			}.bind(this),
			_onCloseComplete: function() {
				this.destroy()
			}.bind(this)
		}
	};
	if (jQuery.type(type) == 'string') {
		this.type = type;
		type = this.defaultOptions[type]
	}
	this.options = jQuery.extend(this.options, type, options);
	if (this.options.id === null) {
		var i = 1;
		while (jQuery('#jBox' + i).length != 0) i++;
		this.options.id = 'jBox' + i
	}
	this.id = this.options.id;
	((this.options.position.x == 'center' && this.options.outside == 'x') || (this.options.position.y == 'center' && this.options.outside == 'y')) && (this.options.outside = false);
	(!this.options.outside || this.options.outside == 'xy') && (this.options.pointer = false);
	jQuery.type(this.options.offset) != 'object' && (this.options.offset = {
		x: this.options.offset,
		y: this.options.offset
	});
	this.options.offset.x || (this.options.offset.x = 0);
	this.options.offset.y || (this.options.offset.y = 0);
	jQuery.type(this.options.adjustDistance) != 'object' && (this.options.adjustDistance = {
		top: this.options.adjustDistance,
		right: this.options.adjustDistance,
		bottom: this.options.adjustDistance,
		left: this.options.adjustDistance
	});
	this.align = (this.options.outside && this.options.outside != 'xy') ? this.options.position[this.options.outside] : (this.options.position.y != 'center' && jQuery.type(this.options.position.y) != 'number' ? this.options.position.x : (this.options.position.x != 'center' && jQuery.type(this.options.position.x) != 'number' ? this.options.position.y : this.options.attributes.x));
	this.options.outside && this.options.outside != 'xy' && (this.outside = this.options.position[this.options.outside]);
	var userAgent = navigator.userAgent.toLowerCase();
	this.IE8 = userAgent.indexOf('msie') != -1 && parseInt(userAgent.split('msie')[1]) == 8;
	this.prefix = userAgent.indexOf('webkit') != -1 ? '-webkit-' : '';
	this._getOpp = function(opp) {
		return {
			left: 'right',
			right: 'left',
			top: 'bottom',
			bottom: 'top',
			x: 'y',
			y: 'x'
		}[opp]
	};
	this._getXY = function(xy) {
		return {
			left: 'x',
			right: 'x',
			top: 'y',
			bottom: 'y',
			center: 'x'
		}[xy]
	};
	this._getTL = function(tl) {
		return {
			left: 'left',
			right: 'left',
			top: 'top',
			bottom: 'top',
			center: 'left',
			x: 'left',
			y: 'top'
		}[tl]
	};
	this._create = function() {
		if (this.wrapper) return;
		this.wrapper = jQuery('<div/>', {
			id: this.id,
			'class': 'jBox-wrapper' + (this.type ? ' jBox-' + this.type : '') + (this.options.theme ? ' jBox-' + this.options.theme : '') + (this.options.addClass ? ' ' + this.options.addClass : '') + (this.IE8 ? ' jBox-IE8' : '')
		}).css({
			position: (this.options.fixed ? 'fixed' : 'absolute'),
			display: 'none',
			opacity: 0,
			zIndex: this.options.zIndex
		}).data('jBox', this);
		this.options.closeOnMouseleave && this.wrapper.mouseenter(function() {
			this.open()
		}.bind(this)).mouseleave(function() {
			this.close()
		}.bind(this));
		this.container = jQuery('<div/>', {
			'class': 'jBox-container'
		}).css({
			width: this.options.width,
			height: this.options.height
		}).appendTo(this.wrapper);
		this.content = jQuery('<div/>', {
			'class': 'jBox-content'
		}).appendTo(this.container);

		if (this.options.closeButton) {
			this.closeButton = jQuery('<div/>', {
				'class': 'jBox-closeButton jBox-noDrag'
			}).click(function() {
				this.close()
			}.bind(this));
			if (this.options.closeButton != 'title') {
				this.wrapper.addClass('jBox-closeButton-box');
				this.closeButton.appendTo(this.container)
			}
		}/*
		this.wrapper.appendTo(this.options.appendTo);
		this.content = jQuery('<div/>', {
			'class': 'jBox-icon'
		}).appendTo(this.container);*/
		
		this.wrapper.appendTo(this.options.appendTo);
		if (this.options.pointer) {
			this.pointer = {
				position: this._getOpp(this.outside),
				xy: this._getXY(this.outside),
				align: 'center',
				offset: 0
			};
			this.pointer.element = jQuery('<div/>', {
				'class': 'jBox-pointer jBox-pointer-' + this.pointer.position
			}).appendTo(this.wrapper);
			this.pointer.dimensions = {
				x: this.pointer.element.outerWidth(),
				y: this.pointer.element.outerHeight()
			};
			if (jQuery.type(this.options.pointer) == 'string') {
				var split = this.options.pointer.split(':');
				split[0] && (this.pointer.align = split[0]);
				split[1] && (this.pointer.offset = parseInt(split[1]))
			}
			this.pointer.alignAttribute = (this.pointer.xy == 'x' ? (this.pointer.align == 'bottom' ? 'bottom' : 'top') : (this.pointer.align == 'right' ? 'right' : 'left'));
			this.wrapper.css('padding-' + this.pointer.position, this.pointer.dimensions[this.pointer.xy]);
			this.pointer.element.css(this.pointer.alignAttribute, (this.pointer.align == 'center' ? '50%' : 0)).css('margin-' + this.pointer.alignAttribute, this.pointer.offset);
			this.pointer.margin = {
				margin: this.pointer.element.css('margin')
			};
			(this.pointer.align == 'center') && this.pointer.element.css(this.prefix + 'transform', 'translate(' + (this.pointer.xy == 'y' ? (this.pointer.dimensions.x * -0.5 + 'px') : 0) + ', ' + (this.pointer.xy == 'x' ? (this.pointer.dimensions.y * -0.5 + 'px') : 0) + ')');
			this.pointer.element.css((this.pointer.xy == 'x' ? 'width' : 'height'), parseInt(this.pointer.dimensions[this.pointer.xy]) + parseInt(this.container.css('border-' + this.pointer.alignAttribute + '-width')));
			this.wrapper.addClass('jBox-pointerPosition-' + this.pointer.position)
		}
		//this.setFooterContent(this.options.footerContent);

		this.setContent(this.options.content);
		this.setTitle(this.options.title);
		if(this.options.footer){
			//this.footerContent = this.options.footerContent.appendTo(this.container);
			this.setFooterContent(this.options.footerContent);
		}
		if (this.options.draggable) {
			var handle = (this.options.draggable == 'title') ? this.titleContainer : (this.options.draggable.length > 0 ? this.options.draggable : this.wrapper);
			handle.addClass('jBox-draggable').on('mousedown', function(ev) {
				if (ev.button == 2 || jQuery(ev.target).hasClass('jBox-noDrag') || jQuery(ev.target).parents('.jBox-noDrag').length) return;
				var drg_h = this.wrapper.outerHeight(),
					drg_w = this.wrapper.outerWidth(),
					pos_y = this.wrapper.offset().top + drg_h - ev.pageY,
					pos_x = this.wrapper.offset().left + drg_w - ev.pageX;
				jQuery(document).on('mousemove.jBox-draggable-' + this.id, function(ev) {
					this.wrapper.offset({
						top: ev.pageY + pos_y - drg_h,
						left: ev.pageX + pos_x - drg_w
					})
				}.bind(this));
				ev.preventDefault()
			}.bind(this)).on('mouseup', function() {
				jQuery(document).off('mousemove.jBox-draggable-' + this.id)
			}.bind(this))
		}(this.options.onCreated.bind(this))();
		this.options._onCreated && (this.options._onCreated.bind(this))()
	};
	this.options.constructOnInit && this._create();
	this.options.attach && this.attach();
	this._positionMouse = function(ev) {
		this.pos = {
			left: ev.pageX,
			top: ev.pageY
		};
		var setPosition = function(a, p) {
				if (this.options.position[p] == 'center') {
					this.pos[a] -= Math.ceil(this.dimensions[p] / 2);
					return
				}
				this.pos[a] += (a == this.options.position[p]) ? ((this.dimensions[p] * -1) - this.options.offset[p]) : this.options.offset[p];
				return this.pos[a]
			}.bind(this);
		this.wrapper.css({
			left: setPosition('left', 'x'),
			top: setPosition('top', 'y')
		});
		this.targetDimensions = {
			x: 0,
			y: 0,
			left: ev.pageX,
			top: ev.pageY
		};
		this._adjustPosition()
	};
	this._attachEvents = function() {
		this.options.closeOnEsc && jQuery(document).on('keyup.jBox-' + this.id, function(ev) {
			if (ev.keyCode == 27) {
				this.close({
					ignoreDelay: true
				})
			}
		}.bind(this));
		this.options.closeOnClick && jQuery(document).on('click.jBox-' + this.id, function(ev) {
			if (this.blockBodyClick || (this.options.closeOnClick == 'box' && ev.target != this.wrapper[0] && !this.wrapper.has(ev.target).length) || (this.options.closeOnClick == 'body' && (ev.target == this.wrapper[0] || this.wrapper.has(ev.target).length))) return;
			this.close({
				ignoreDelay: true
			})
		}.bind(this));
		if (((this.options.adjustPosition && this.options.adjustTracker) || this.options.reposition) && !this.fixed && this.outside) {
			var scrollTimer, scrollTimerTriggered = 0,
				scrollTriggerDelay = 150;
			var positionDelay = function() {
					var now = new Date().getTime();
					if (!scrollTimer) {
						if (now - scrollTimerTriggered > scrollTriggerDelay) {
							this.options.reposition && this.position();
							this.options.adjustTracker && this._adjustPosition();
							scrollTimerTriggered = now
						}
						scrollTimer = setTimeout(function() {
							scrollTimer = null;
							scrollTimerTriggered = new Date().getTime();
							this.options.reposition && this.position();
							this.options.adjustTracker && this._adjustPosition()
						}.bind(this), scrollTriggerDelay)
					}
				}.bind(this);
			(this.options.adjustTracker && this.options.adjustTracker != 'resize') && jQuery(window).on('scroll.jBox-' + this.id, function(ev) {
				positionDelay()
			}.bind(this));
			((this.options.adjustTracker && this.options.adjustTracker != 'scroll') || this.options.reposition) && jQuery(window).on('resize.jBox-' + this.id, function(ev) {
				positionDelay()
			}.bind(this))
		}
		this.options.target == 'mouse' && jQuery('body').on('mousemove.jBox-' + this.id, function(ev) {
			this._positionMouse(ev)
		}.bind(this))
	};
	this._detachEvents = function() {
		this.options.closeOnEsc && jQuery(document).off('keyup.jBox-' + this.id);
		this.options.closeOnClick && jQuery(document).off('click.jBox-' + this.id);
		if ((this.options.adjustPosition && this.options.adjustTracker) || this.options.reposition) {
			jQuery(window).off('scroll.jBox-' + this.id);
			jQuery(window).off('resize.jBox-' + this.id)
		}
		this.options.target == 'mouse' && jQuery('body').off('mousemove.jBox-' + this.id)
	};
	this._addOverlay = function() {
		!this.overlay && (this.overlay = jQuery('#jBox-overlay').length ? jQuery('#jBox-overlay').css({
			zIndex: Math.min(jQuery('#jBox-overlay').css('z-index'), (this.options.zIndex - 1))
		}) : (jQuery('<div/>', {
			id: 'jBox-overlay'
		}).css({
			display: 'none',
			opacity: 0,
			zIndex: (this.options.zIndex - 1)
		}).appendTo(jQuery('body'))));
		var overlay_data = this.overlay.data('jBox') || {};
		overlay_data['jBox-' + this.id] = true;
		this.overlay.data('jBox', overlay_data);
		if (this.overlay.css('display') == 'block') return;
		this.options.fade ? (this.overlay.stop() && this.overlay.animate({
			opacity: 1
		}, {
			queue: false,
			duration: this.options.fade,
			start: function() {
				this.overlay.css({
					display: 'block'
				})
			}.bind(this)
		})) : this.overlay.css({
			display: 'block',
			opacity: 1
		})
	};
	this._removeOverlay = function() {
		if (!this.overlay) return;
		var overlay_data = this.overlay.data('jBox');
		delete overlay_data['jBox-' + this.id];
		this.overlay.data('jBox', overlay_data);
		if (jQuery.isEmptyObject(overlay_data)) {
			this.options.fade ? (this.overlay.stop() && this.overlay.animate({
				opacity: 0
			}, {
				queue: false,
				duration: this.options.fade,
				complete: function() {
					this.overlay.css({
						display: 'none'
					})
				}.bind(this)
			})) : this.overlay.css({
				display: 'none',
				opacity: 0
			})
		}
	};
	this._generateCSS = function() {
		if (this.IE8) return;
		(jQuery.type(this.options.animation) != 'object') && (this.options.animation = {
			pulse: {
				open: 'pulse',
				close: 'zoomOut'
			},
			zoomIn: {
				open: 'zoomIn',
				close: 'zoomIn'
			},
			zoomOut: {
				open: 'zoomOut',
				close: 'zoomOut'
			},
			move: {
				open: 'move',
				close: 'move'
			},
			slide: {
				open: 'slide',
				close: 'slide'
			},
			flip: {
				open: 'flip',
				close: 'flip'
			},
			tada: {
				open: 'tada',
				close: 'zoomOut'
			}
		}[this.options.animation]);
		this.options.animation.open && (this.options.animation.open = this.options.animation.open.split(':'));
		this.options.animation.close && (this.options.animation.close = this.options.animation.close.split(':'));
		this.options.animation.openDirection = this.options.animation.open ? this.options.animation.open[1] : null;
		this.options.animation.closeDirection = this.options.animation.close ? this.options.animation.close[1] : null;
		this.options.animation.open && (this.options.animation.open = this.options.animation.open[0]);
		this.options.animation.close && (this.options.animation.close = this.options.animation.close[0]);
		this.options.animation.open && (this.options.animation.open += 'Open');
		this.options.animation.close && (this.options.animation.close += 'Close');
		var animations = {
			pulse: {
				duration: 350,
				css: [
					['0%', 'scale(1)'],
					['50%', 'scale(1.1)'],
					['100%', 'scale(1)']
				]
			},
			zoomInOpen: {
				duration: (this.options.fade || 180),
				css: [
					['0%', 'scale(0.9)'],
					['100%', 'scale(1)']
				]
			},
			zoomInClose: {
				duration: (this.options.fade || 180),
				css: [
					['0%', 'scale(1)'],
					['100%', 'scale(0.9)']
				]
			},
			zoomOutOpen: {
				duration: (this.options.fade || 180),
				css: [
					['0%', 'scale(1.1)'],
					['100%', 'scale(1)']
				]
			},
			zoomOutClose: {
				duration: (this.options.fade || 180),
				css: [
					['0%', 'scale(1)'],
					['100%', 'scale(1.1)']
				]
			},
			moveOpen: {
				duration: (this.options.fade || 180),
				positions: {
					top: {
						'0%': -12
					},
					right: {
						'0%': 12
					},
					bottom: {
						'0%': 12
					},
					left: {
						'0%': -12
					}
				},
				css: [
					['0%', 'translate%XY(%Vpx)'],
					['100%', 'translate%XY(0px)']
				]
			},
			moveClose: {
				duration: (this.options.fade || 180),
				timing: 'ease-in',
				positions: {
					top: {
						'100%': -12
					},
					right: {
						'100%': 12
					},
					bottom: {
						'100%': 12
					},
					left: {
						'100%': -12
					}
				},
				css: [
					['0%', 'translate%XY(0px)'],
					['100%', 'translate%XY(%Vpx)']
				]
			},
			slideOpen: {
				duration: 400,
				positions: {
					top: {
						'0%': -400
					},
					right: {
						'0%': 400
					},
					bottom: {
						'0%': 400
					},
					left: {
						'0%': -400
					}
				},
				css: [
					['0%', 'translate%XY(%Vpx)'],
					['100%', 'translate%XY(0px)']
				]
			},
			slideClose: {
				duration: 400,
				timing: 'ease-in',
				positions: {
					top: {
						'100%': -400
					},
					right: {
						'100%': 400
					},
					bottom: {
						'100%': 400
					},
					left: {
						'100%': -400
					}
				},
				css: [
					['0%', 'translate%XY(0px)'],
					['100%', 'translate%XY(%Vpx)']
				]
			},
			flipOpen: {
				duration: 600,
				css: [
					['0%', 'perspective(400px) rotateX(90deg)'],
					['40%', 'perspective(400px) rotateX(-15deg)'],
					['70%', 'perspective(400px) rotateX(15deg)'],
					['100%', 'perspective(400px) rotateX(0deg)']
				]
			},
			flipClose: {
				duration: (this.options.fade || 300),
				css: [
					['0%', 'perspective(400px) rotateX(0deg)'],
					['100%', 'perspective(400px) rotateX(90deg)']
				]
			},
			tada: {
				duration: 800,
				css: [
					['0%', 'scale(1)'],
					['10%, 20%', 'scale(0.9) rotate(-3deg)'],
					['30%, 50%, 70%, 90%', 'scale(1.1) rotate(3deg)'],
					['40%, 60%, 80%', 'scale(1.1) rotate(-3deg)'],
					['100%', 'scale(1) rotate(0)']
				]
			}
		};
		jQuery.each(['pulse', 'tada'], function(index, item) {
			animations[item + 'Open'] = animations[item + 'Close'] = animations[item]
		});
		var generateKeyframeCSS = function(ev, position) {
				keyframe_css = '@' + this.prefix + 'keyframes jBox-animation-' + this.options.animation[ev] + '-' + ev + (position ? '-' + position : '') + ' {';
				jQuery.each(animations[this.options.animation[ev]].css, function(index, item) {
					var translate = position ? item[1].replace('%XY', this._getXY(position).toUpperCase()) : item[1];
					animations[this.options.animation[ev]].positions && (translate = translate.replace('%V', animations[this.options.animation[ev]].positions[position][item[0]]));
					keyframe_css += item[0] + ' {' + this.prefix + 'transform:' + translate + ';}'
				}.bind(this));
				keyframe_css += '}';
				keyframe_css += '.jBox-animation-' + this.options.animation[ev] + '-' + ev + (position ? '-' + position : '') + ' {';
				keyframe_css += this.prefix + 'animation-duration: ' + animations[this.options.animation[ev]].duration + 'ms;';
				keyframe_css += this.prefix + 'animation-name: jBox-animation-' + this.options.animation[ev] + '-' + ev + (position ? '-' + position : '') + ';';
				keyframe_css += animations[this.options.animation[ev]].timing ? (this.prefix + 'animation-timing-function: ' + animations[this.options.animation[ev]].timing + ';') : '';
				keyframe_css += '}';
				return keyframe_css
			}.bind(this);
		var css = '';
		jQuery.each(['open', 'close'], function(index, ev) {
			if (!this.options.animation[ev] || !animations[this.options.animation[ev]] || (ev == 'close' && !this.options.fade)) return '';
			animations[this.options.animation[ev]].positions ? jQuery.each(['top', 'right', 'bottom', 'left'], function(index2, position) {
				css += generateKeyframeCSS(ev, position)
			}) : css += generateKeyframeCSS(ev)
		}.bind(this));
		jQuery('<style/>').append(css).appendTo(jQuery('head'))
	};
	this._blockBodyClick = function() {
		this.blockBodyClick = true;
		setTimeout(function() {
			this.blockBodyClick = false
		}.bind(this), 10)
	};
	this.options.animation && this._generateCSS();
	this._animate = function(ev) {
		if (this.IE8) return;
		ev || (ev = this.isOpen ? 'open' : 'close');
		if (!this.options.fade && ev == 'close') return null;
		var animationDirection = (this.options.animation[ev + 'Direction'] || ((this.align != 'center') ? this.align : this.options.attributes.x));
		this.flipped && this._getXY(animationDirection) == (this._getXY(this.align)) && (animationDirection = this._getOpp(animationDirection));
		var classnames = 'jBox-animation-' + this.options.animation[ev] + '-' + ev + ' jBox-animation-' + this.options.animation[ev] + '-' + ev + '-' + animationDirection;
		this.wrapper.addClass(classnames);
		var animationDuration = parseFloat(this.wrapper.css(this.prefix + 'animation-duration')) * 1000;
		ev == 'close' && (animationDuration = Math.min(animationDuration, this.options.fade));
		setTimeout(function() {
			this.wrapper.removeClass(classnames)
		}.bind(this), animationDuration)
	};
	this._abortAnimation = function() {
		if (this.IE8) return;
		var prefix = 'jBox-animation';
		var classes = this.wrapper.attr('class').split(' ').filter(function(c) {
			return c.lastIndexOf(prefix, 0) !== 0
		});
		this.wrapper.attr('class', classes.join(' '))
	};
	this._adjustPosition = function() {
		if (!this.options.adjustPosition) return null;
		if (this.positionAdjusted) {
			this.wrapper.css(this.pos);
			this.pointer && this.wrapper.css('padding', 0).css('padding-' + this._getOpp(this.outside), this.pointer.dimensions[this._getXY(this.outside)]).removeClass('jBox-pointerPosition-' + this._getOpp(this.pointer.position)).addClass('jBox-pointerPosition-' + this.pointer.position);
			this.pointer && this.pointer.element.attr('class', 'jBox-pointer jBox-pointer-' + this._getOpp(this.outside)).css(this.pointer.margin);
			this.positionAdjusted = false;
			this.flipped = false
		}
		var win = jQuery(window);
		var windowDimensions = {
			x: win.width(),
			y: win.height(),
			top: win.scrollTop(),
			left: win.scrollLeft()
		};
		windowDimensions.bottom = windowDimensions.top + windowDimensions.y;
		windowDimensions.right = windowDimensions.left + windowDimensions.x;
		var outYT = (windowDimensions.top > this.pos.top - (this.options.adjustDistance.top || 0)),
			outXR = (windowDimensions.right < this.pos.left + this.dimensions.x + (this.options.adjustDistance.right || 0)),
			outYB = (windowDimensions.bottom < this.pos.top + this.dimensions.y + (this.options.adjustDistance.bottom || 0)),
			outXL = (windowDimensions.left > this.pos.left - (this.options.adjustDistance.left || 0)),
			outX = outXL ? 'left' : (outXR ? 'right' : null),
			outY = outYT ? 'top' : (outYB ? 'bottom' : null),
			out = outX || outY;
		if (!out) return;
		if (this.options.adjustPosition != 'move' && (outX == this.outside || outY == this.outside)) {
			this.target == 'mouse' && (this.outside = 'right');
			if (((this.outside == 'top' || this.outside == 'left') ? (windowDimensions[this._getXY(this.outside)] - (this.targetDimensions[this._getTL(this.outside)] - windowDimensions[this._getTL(this.outside)]) - this.targetDimensions[this._getXY(this.outside)]) : (this.targetDimensions[this._getTL(this.outside)] - windowDimensions[this._getTL(this.outside)])) > this.dimensions[this._getXY(this.outside)] + this.options.adjustDistance[this._getOpp(this.outside)]) {
				this.wrapper.css(this._getTL(this.outside), this.pos[this._getTL(this.outside)] + ((this.dimensions[this._getXY(this.outside)] + this.options.offset[this._getXY(this.outside)] + this.targetDimensions[this._getXY(this.outside)]) * (this.outside == 'top' || this.outside == 'left' ? 1 : -1))).removeClass('jBox-pointerPosition-' + this.pointer.position).addClass('jBox-pointerPosition-' + this._getOpp(this.pointer.position));
				this.pointer && this.wrapper.css('padding', 0).css('padding-' + this.outside, this.pointer.dimensions[this._getXY(this.outside)]);
				this.pointer && this.pointer.element.attr('class', 'jBox-pointer jBox-pointer-' + this.outside);
				this.positionAdjusted = true;
				this.flipped = true
			}
		}
		var outMove = (this._getXY(this.outside) == 'x') ? outY : outX;
		if (this.pointer && this.options.adjustPosition != 'flip' && this._getXY(outMove) == this._getOpp(this._getXY(this.outside))) {
			if (this.pointer.align == 'center') {
				var spaceAvail = (this.dimensions[this._getXY(outMove)] / 2) - (this.pointer.dimensions[this._getOpp(this.pointer.xy)] / 2) - (parseInt(this.pointer.element.css('margin-' + this.pointer.alignAttribute)) * (outMove != this._getTL(outMove) ? -1 : 1))
			} else {
				var spaceAvail = (outMove == this.pointer.alignAttribute) ? parseInt(this.pointer.element.css('margin-' + this.pointer.alignAttribute)) : this.dimensions[this._getXY(outMove)] - parseInt(this.pointer.element.css('margin-' + this.pointer.alignAttribute)) - this.pointer.dimensions[this._getXY(outMove)]
			}
			spaceDiff = (outMove == this._getTL(outMove)) ? windowDimensions[this._getTL(outMove)] - this.pos[this._getTL(outMove)] + this.options.adjustDistance[outMove] : (windowDimensions[this._getOpp(this._getTL(outMove))] - this.pos[this._getTL(outMove)] - this.options.adjustDistance[outMove] - this.dimensions[this._getXY(outMove)]) * -1;
			if (outMove == this._getOpp(this._getTL(outMove)) && this.pos[this._getTL(outMove)] - spaceDiff < windowDimensions[this._getTL(outMove)] + this.options.adjustDistance[this._getTL(outMove)]) {
				spaceDiff -= windowDimensions[this._getTL(outMove)] + this.options.adjustDistance[this._getTL(outMove)] - (this.pos[this._getTL(outMove)] - spaceDiff)
			}
			spaceDiff = Math.min(spaceDiff, spaceAvail);
			if (spaceDiff <= spaceAvail && spaceDiff > 0) {
				this.pointer.element.css('margin-' + this.pointer.alignAttribute, parseInt(this.pointer.element.css('margin-' + this.pointer.alignAttribute)) - (spaceDiff * (outMove != this.pointer.alignAttribute ? -1 : 1)));
				this.wrapper.css(this._getTL(outMove), this.pos[this._getTL(outMove)] + (spaceDiff * (outMove != this._getTL(outMove) ? -1 : 1)));
				this.positionAdjusted = true
			}
		}
	};
	(this.options.onInit.bind(this))();
	this.options._onInit && (this.options._onInit.bind(this))();
	return this
};
jBox.prototype.attach = function(elements, trigger) {
	elements || (elements = this.options.attach);
	trigger || (trigger = this.options.trigger);
	elements && elements.length && jQuery.each(elements, function(index, el) {
		el = jQuery(el);
		if (!el.data('jBox-attached-' + this.id)) {
			(this.options.getContent == 'title' && el.attr('title') != undefined) && el.data('jBox-getContent', el.attr('title')).removeAttr('title');
			this.attachedElements || (this.attachedElements = []);
			this.attachedElements.push(el[0]);
			el.on(trigger + '.jBox-attach-' + this.id, function(ev) {
				if (this.isOpen && this.source[0] != el[0]) var forceOpen = true;
				this.source = el;
				!this.options.target && (this.target = el);
				trigger == 'click' && this.options.preventDefault && ev.preventDefault();
				this[trigger == 'click' && !forceOpen ? 'toggle' : 'open']()
			}.bind(this));
			(this.options.trigger == 'mouseenter') && el.on('mouseleave', function() {
				this.close()
			}.bind(this));
			el.data('jBox-attached-' + this.id, trigger)
		}
	}.bind(this));
	return this
};
jBox.prototype.detach = function(elements) {
	elements || (elements = this.attachedElements || []);
	elements && elements.length && jQuery.each(elements, function(index, el) {
		el = jQuery(el);
		if (el.data('jBox-attached-' + this.id)) {
			el.off(el.data('jBox-attached-' + this.id + '.jBox-attach-' + this.id));
			el.data('jBox-attached-' + this.id, null)
		}
	});
	return this
};
jBox.prototype.setTitle = function(title) {
	if (title == null || title == undefined) return this;
	!this.wrapper && this._create();
	if (!this.title) {
		this.titleContainer = jQuery('<div/>', {
			'class': 'jBox-title'
		});
		this.title = jQuery('<div/>').appendTo(this.titleContainer);
		this.wrapper.addClass('jBox-hasTitle');
		if (this.options.closeButton == 'title') {
			this.wrapper.addClass('jBox-closeButton-title');
			this.closeButton.appendTo(this.titleContainer)
		}
		this.titleContainer.insertBefore(this.content)
	}
	this.title.html(title);
	this.position();
	return this
};
jBox.prototype.setContent = function(content) {
	if (content == null) return this;
	!this.wrapper && this._create();
	switch (jQuery.type(content)) {
	case 'string':
		this.content.html(content);
		break;
	case 'object':
		this.content.children().css({
			display: 'none'
		});
		this.options.content.appendTo(this.content).css({
			display: 'inline-block'
		});
		break
	}
	this.position();
	return this
};
jBox.prototype.setFooterContent = function(content) {
	if (content == null) return this;
	!this.wrapper && this._create();
	if (content) {
		this.footerContent = jQuery('<div/>', {
			'class': 'jBox-footer'
		}).append(content);
		this.footerContent.insertAfter(this.content)
	}
	this.position();
	return this

};
jBox.prototype.position = function(options) {
	options || (options = {});
	this.target = options.target || this.target || this.options.target || jQuery(window);
	this.dimensions = {
		x: this.wrapper.outerWidth(),
		y: this.wrapper.outerHeight()
	};
	if (this.target == 'mouse') return;
	if (this.options.position.x == 'center' && this.options.position.y == 'center') {
		this.wrapper.css({
			left: '50%',
			top: '50%',
			marginLeft: (this.dimensions.x * -0.5 + this.options.offset.x),
			marginTop: (this.dimensions.y * -0.5 + this.options.offset.y)
		});
		return this
	}
	var targetOffset = this.target.offset();
	this.targetDimensions = {
		x: this.target.outerWidth(),
		y: this.target.outerHeight(),
		top: (targetOffset ? targetOffset.top : 0),
		left: (targetOffset ? targetOffset.left : 0)
	};
	this.pos = {};
	var setPosition = function(p) {
			if (jQuery.inArray(this.options.position[p], ['top', 'right', 'bottom', 'left', 'center']) == -1) {
				this.pos[this.options.attributes[p]] = this.options.position[p];
				return
			}
			var a = this.options.attributes[p] = (p == 'x' ? 'left' : 'top');
			this.pos[a] = this.targetDimensions[a];
			if (this.options.position[p] == 'center') {
				this.pos[a] += Math.ceil((this.targetDimensions[p] - this.dimensions[p]) / 2);
				return
			}(a != this.options.position[p]) && (this.pos[a] += this.targetDimensions[p] - this.dimensions[p]);
			(this.options.outside == p || this.options.outside == 'xy') && (this.pos[a] += this.dimensions[p] * (a != this.options.position[p] ? 1 : -1))
		}.bind(this);
	setPosition('x');
	setPosition('y');
	if (this.options.pointer) {
		var adjustWrapper = 0;
		switch (this.pointer.align) {
		case 'center':
			if (this.options.position[this._getOpp(this.options.outside)] != 'center') {
				adjustWrapper += (this.dimensions[this._getOpp(this.options.outside)] / 2)
			}
			break;
		default:
			switch (this.options.position[this._getOpp(this.options.outside)]) {
			case 'center':
				adjustWrapper += ((this.dimensions[this._getOpp(this.options.outside)] / 2) - (this.pointer.dimensions[this._getOpp(this.options.outside)] / 2)) * (this.pointer.align == this._getTL(this.pointer.align) ? 1 : -1);
				break;
			default:
				adjustWrapper += (this.pointer.align != this.options.position[this._getOpp(this.options.outside)]) ? this.dimensions[this._getOpp(this.options.outside)] - (this.pointer.dimensions[this._getOpp(this.options.outside)] / 2) : (this.pointer.dimensions[this._getOpp(this.options.outside)] / 2);
				break
			}
			break
		}
		adjustWrapper *= (this.options.position[this._getOpp(this.options.outside)] == this.pointer.alignAttribute ? -1 : 1);
		adjustWrapper += this.pointer.offset * (this.pointer.align == this._getOpp(this._getTL(this.pointer.align)) ? 1 : -1);
		this.pos[this._getTL(this._getOpp(this.pointer.xy))] += adjustWrapper
	}
	this.pos[this.options.attributes.x] += this.options.offset.x;
	this.pos[this.options.attributes.y] += this.options.offset.y;
	this.wrapper.css(this.pos);
	this._adjustPosition();
	return this
};
jBox.prototype.open = function(options) {
	options || (options = {});
	!this.wrapper && this._create();
	this.timer && clearTimeout(this.timer);
	this._blockBodyClick();
	if (this.isDisabled) return this;
	var open = function() {
			this.source && this.options.getTitle && (this.source.attr(this.options.getTitle) != undefined && this.setTitle(this.source.attr(this.options.getTitle)));
			this.source && this.options.getContent && (this.source.data('jBox-getContent') != undefined ? this.setContent(this.source.data('jBox-getContent')) : (this.source.attr(this.options.getContent) != undefined ? this.setContent(this.source.attr(this.options.getContent)) : null));
			this.position({
				target: options.target
			});
			(this.options.onOpen.bind(this))();
			this.options._onOpen && (this.options._onOpen.bind(this))();
			this.options.ajax && (!this.ajaxLoaded || this.options.reload) && this.ajax();
			this.isClosing && this._abortAnimation();
			if (!this.isOpen) {
				this.isOpen = true;
				this._attachEvents();
				this.options.blockScroll && jQuery('body').addClass('jBox-blockScroll-' + this.id);
				this.options.overlay && this._addOverlay();
				this.options.animation && !this.isClosing && this._animate('open');
				if (this.options.fade) {
					this.wrapper.stop().animate({
						opacity: 1
					}, {
						queue: false,
						duration: this.options.fade,
						start: function() {
							this.isOpening = true;
							this.wrapper.css({
								display: 'block'
							})
						}.bind(this),
						always: function() {
							this.isOpening = false
						}.bind(this)
					})
				} else {
					this.wrapper.css({
						display: 'block',
						opacity: 1
					})
				}
			}
		}.bind(this);
	this.options.delayOpen && !this.isOpen && !this.isClosing && !options.ignoreDelay ? (this.timer = setTimeout(open, this.options.delayOpen)) : open();
	return this
};
jBox.prototype.close = function(options) {
	options || (options = {});
	this.timer && clearTimeout(this.timer);
	this._blockBodyClick();
	if (this.isDisabled) return this;
	var close = function() {
			(this.options.onClose.bind(this))();
			if (this.isOpen) {
				this.isOpen = false;
				this._detachEvents();
				this.options.blockScroll && jQuery('body').removeClass('jBox-blockScroll-' + this.id);
				this.options.overlay && this._removeOverlay();
				this.options.animation && !this.isOpening && this._animate('close');
				this.source = null;
				if (this.options.fade) {
					this.wrapper.stop().animate({
						opacity: 0
					}, {
						queue: false,
						duration: this.options.fade,
						start: function() {
							this.isClosing = true
						}.bind(this),
						complete: function() {
							this.wrapper.css({
								display: 'none'
							});
							this.options._onCloseComplete && (this.options._onCloseComplete.bind(this))()
						}.bind(this),
						always: function() {
							this.isClosing = false
						}.bind(this),
					})
				} else {
					this.wrapper.css({
						display: 'none',
						opacity: 0
					});
					this.options._onCloseComplete && (this.options._onCloseComplete.bind(this))()
				}
			}
		}.bind(this);
	options.ignoreDelay ? close() : (this.timer = setTimeout(close, Math.max(this.options.delayClose, 10)));
	return this
};
jBox.prototype.toggle = function(options) {
	this[this.isOpen ? 'close' : 'open'](options);
	return this
};
jBox.prototype.disable = function() {
	this.isDisabled = true;
	return this
};
jBox.prototype.enable = function() {
	this.isDisabled = false;
	return this
};
jBox.prototype.ajax = function(options) {
	options || (options = {});
	this.ajaxRequest && this.ajaxRequest.abort();
	this.ajaxRequest = jQuery.ajax({
		url: options.url || this.options.ajax,
		data: options.data || this.options.data,
		beforeSend: function() {
			this.content.html('');
			this.wrapper.addClass('jBox-loading');
			this.position();
			(this.options.onAjax.bind(this))()
		}.bind(this),
		complete: function(response) {
			this.wrapper.removeClass('jBox-loading');
			this.content.html(response.responseText);
			this.position();
			this.ajaxLoaded = true;
			(this.options.onAjaxComplete.bind(this))()
		}.bind(this)
	});
	return this
};
jBox.prototype.destroy = function() {
	this.close({
		ignoreDelay: true
	});
	this.wrapper.remove();
	return this
};
jQuery.fn.jBox = function(type, options) {
	type || (type = {});
	options || (options = {});
	return new jBox(type, jQuery.extend(options, {
		attach: this
	}))
};
if (!Function.prototype.bind) {
	Function.prototype.bind = function(oThis) {
		var aArgs = Array.prototype.slice.call(arguments, 1),
			fToBind = this,
			fNOP = function() {},
			fBound = function() {
				return fToBind.apply(this instanceof fNOP && oThis ? this : oThis, aArgs.concat(Array.prototype.slice.call(arguments)))
			};
		fNOP.prototype = this.prototype;
		fBound.prototype = new fNOP();
		return fBound
	}
}
$(document).ready(function() {
	$('#stephan').mouseenter(function() {
		var ring = $('<div/>').addClass('stephan-ring').appendTo($('#stephan'));
		setTimeout((function() {
			ring.remove()
		}), 600)
	});
	if (($('#content').height() + $('#footer').outerHeight()) < $(window).height()) {
		$('#content').css('min-height', ($(window).height() - parseInt($('#content').css('padding-top')) - parseInt($('#content').css('padding-bottom')) - $('#footer').outerHeight()))
	}
});
