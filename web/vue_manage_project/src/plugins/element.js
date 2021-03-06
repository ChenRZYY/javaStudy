import Vue from 'vue'
import {
  Button, // 按钮
  Form, // 表单
  FormItem, // 表单 前文字
  Input, // 表单输入框
  Message, // 消息
  Container, // 布局组件
  Header, // 布局组件 头
  Aside, // 布局组件 侧边栏
  Main, // 布局组件 内容区域
  Menu, // 菜单按钮
  Submenu, // 菜单按钮
  MenuItem, // 菜单按钮
  Breadcrumb,
  BreadcrumbItem,
  Card, // 卡片
  Row, // Layout行布局
  Col, //  Layout列布局
  Table, // 表格 
  TableColumn,
  Switch,
  Tooltip,
  Pagination, // 分页
  Dialog,
  MessageBox,
  Tag, // 标签组件
  Tree, // tree-table 表格tree
  Select, // 选择框
  Option, // 选择option
  Cascader, // el-cascader 级联选择器
  Alert,
  Tabs, // el-tabs 整个tab栏区域
  TabPane, // el-tab-pane 每步骤tab 
  Steps, // el-step步骤条组件
  Step, // el-steps 步骤条组件
  CheckboxGroup,
  Checkbox,
  Upload,
  Timeline,
  TimelineItem
} from 'element-ui'

// import Timeline from './timeline/index.js'
// import TimelineItem from './timeline-item/index.js'

Vue.use(Button)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Input)
Vue.use(Container)
Vue.use(Header)
Vue.use(Aside)
Vue.use(Main)
Vue.use(Menu)
Vue.use(Submenu)
Vue.use(MenuItem)
Vue.use(Breadcrumb)
Vue.use(BreadcrumbItem)
Vue.use(Card)
Vue.use(Row)
Vue.use(Col)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(Switch)
Vue.use(Tooltip)
Vue.use(Pagination)
Vue.use(Dialog)
Vue.use(Tag)
Vue.use(Tree)
Vue.use(Select)
Vue.use(Option)
Vue.use(Cascader)
Vue.use(Alert)
Vue.use(Tabs)
Vue.use(TabPane)
Vue.use(Steps)
Vue.use(Step)
Vue.use(CheckboxGroup)
Vue.use(Checkbox)
Vue.use(Upload)
Vue.use(Timeline)
Vue.use(TimelineItem)
// Vue.use(Timeline)
// Vue.use(TimelineItem)
// 用use设置全局组件
Vue.prototype.$message = Message // 这个组件需要全局挂载,其他.vue文件中国直接通过this.$message就可以访问
Vue.prototype.$confirm = MessageBox.confirm
