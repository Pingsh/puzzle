# puzzle
一个拼图小游戏, 算法进行了借鉴

### 注意事项
* 6.0及以上, 需要申请权限, 否则程序会直接崩溃
* 之前有个小BUG, 在MainActivity中,如果弹出难度选择框的控件,注册时 setOnClickListener(this),并不能弹出PopupWindow,原因暂不知晓
* 当选择图片来源为相机拍照,在MainActivity的onActivityResult方法中,data == null.缓存的图片在打开本地相册时无法识别,之后再进行完善

### 猜想
* 关于setOnClickListener的BUG,目前的猜想是,目标控件和PopupWindow中的控件并不同属于一个view...
### 实际情况
* 代码执行了,PopupWindow也显示了,但快速dismiss了...笨笨
