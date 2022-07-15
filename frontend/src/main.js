import Vue from 'vue'
import { Form, FormItem, Input, Select, Option, Upload, Message, Loading } from 'element-ui'
import App from './App.vue'

Vue.component(Form.name, Form)
Vue.component(FormItem.name, FormItem)
Vue.component(Input.name, Input)
Vue.component(Select.name, Select)
Vue.component(Option.name, Option)
Vue.component(Upload.name, Upload)

Vue.prototype.$message = Message
Vue.prototype.$loading = Loading.service

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')
