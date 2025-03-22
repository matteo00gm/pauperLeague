import { createApp } from 'vue'
import App from './App.vue'
import router from './router.js'
import store from './store/index.js'

import BaseButton from './components/ui/BaseButton.vue'
import BaseDialog from './components/ui/BaseDialog.vue'
import BaseConfirmDialog from './components/ui/BaseConfirmDialog.vue'

const app= createApp(App)

app.use(router)
app.use(store)

app.component('base-button', BaseButton)
app.component('base-dialog', BaseDialog)
app.component('base-confirm-dialog', BaseConfirmDialog)

app.mount('#app')

