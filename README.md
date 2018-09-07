# WPopup - 一个简单使用并且高度定制的Popupwindow

<p >
	<a><img src="https://img.shields.io/github/release/wanglu1209/WPopup.svg"/></a>
  	<a><img src="https://img.shields.io/github/last-commit/wanglu1209/WPopup.svg"/></a>
	<a><img src="https://img.shields.io/github/issues/wanglu1209/WPopup.svg"/></a>
	<a><img src="https://img.shields.io/github/issues-closed/wanglu1209/WPopup.svg"/></a>
	<a><img src="https://img.shields.io/github/issues-pr/wanglu1209/WPopup.svg"/></a>
	<a><img src="https://img.shields.io/github/issues-pr-closed/wanglu1209/WPopup.svg"/></a>
	<a><img src="https://img.shields.io/github/forks/wanglu1209/WPopup.svg"/></a>
	<a><img src="https://img.shields.io/github/stars/wanglu1209/WPopup.svg"/></a>
</p>

<img src="https://raw.githubusercontent.com/wanglu1209/WPopup/master/img/gif.gif" width="200" hegiht="400" align=center />

<br/>
<br/>
<br/>

- 自动设置show的位置
- 自动设置倒三角的位置
- 跟随手指点按位置弹出
- 高度定制

## 依赖

```Gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
        implementation 'com.github.wanglu1209:WPopup:lastReleases'
}
	
```


## 使用

### 普通操作

```Kotlin
// 创建WPopup
val pop = WPopup.Builder(this)
      .setData(data)    // 设置数据，数据类为WPopupModel
      .setCancelable(false) // 设置是否能点击外面dismiss
      .setPopupOrientation(WPopup.Builder.HORIZONTAL)   // 设置item排列方向 默认为竖向排列
      .setOnItemClickListener(object : WPopup.Builder.OnItemClickListener {
          override fun onItemClick(view: View, position: Int) {
              // 设置item点击事件
              Toast.makeText(view.context, data[position].text, Toast.LENGTH_LONG).show()
          }
      })
      .create()

```

#### 根据View自动设置显示位置
```Kotlin      
pop.showAtView(view)
```

#### 根据view手动设置显示位置
```Kotlin      
pop.showAtDirectionByView(view, WPopupDirection.LEFT)
```

#### 自己选择弹出位置  上下左右
```Kotlin
pop.showAtDirection(WPopupDirection.BOTTOM)
```

<br/>
<br/>

### 骚操作 根据手指点击位置来显示

```Kotlin
val longClickPop = WPopup.Builder(this)
      .setData(longData)
      .setPopupOrientation(WPopup.Builder.VERTICAL)
      .setClickView(longClickView) // 一定要设置点击的view，用来注册事件
      .setOnItemClickListener(object : WPopup.Builder.OnItemClickListener {
          override fun onItemClick(view: View, position: Int) {
              Toast.makeText(view.context, "$position", Toast.LENGTH_LONG).show()
          }
      })
      .create()
```

#### 自动挑选位置 默认为右下方

```Kotlin
longClickPop.showAtFingerLocation()
```

#### 手动挑选位置

```Kotlin
longClickPop.showAtFingerLocation(WPopupDirection.TOP)
```



<br/><br/><br/>

### 方法及参数

**WPopup已经为您设置好了通用的UI，您只需要自己配置参数即可**


方法名|参数类型|默认值|备注
:---|:--|:---|:--
setData|List<WPopupModel>|null|设置数据 参数为一个字符串，一个图片的resId
setCancelable|Boolean|false|设置点击外面是否能dismiss
setOnItemClickListener|OnItemClickListener|null|设置item点击事件
setPopupOrientation|String|WPopupWindow.Builder.VERTICAL|设置item的排列方向
setDividerColor|Int|Color.WHITE|设置分割线的颜色 
setDividerSize|Int|1|设置分割线的粗细
setDividerMargin|Int|10|设置分割线边距
setIsDim|Boolean|false|设置弹出时背景是否半透明
setDimValue|Float|0.4f|设置背景半透明的值  0.1f - 1f 值越大，越接近透明
setPopupBgColor|Int|Color.parseColor("#CC000000")|设置弹出背景颜色
setPopupMargin|Int|1|设置弹出时和view的距离
setClickView|View|null|设置长按事件的view
setAnim|Int|WPopupAnim.ANIM_ALPHA|设置动画
setIconDirection|Int|WPopupDirection.LEFT|设置icon的方向 如果data中设置了图片的话
setTextColor|Int|Color.WHITE|设置item中text的颜色
setTextSize|Int|14|设置item中text的大小


**当然，如果默认的UI不适合您，您也可以自定义UI，只需传入一些参数即可**

```Kotlin
val customPopup = BasePopup(
      WPopParams(
              R.layout.view_custom, // layoutRes，必须参数
              this, // activity，必须参数
              true, // 背景是否变暗，非必须参数
              cancelable = false,
              width = ViewGroup.LayoutParams.MATCH_PARENT, // popup的宽 只支持LayoutParams里的
              height = ViewGroup.LayoutParams.WRAP_CONTENT
      )
)

```



