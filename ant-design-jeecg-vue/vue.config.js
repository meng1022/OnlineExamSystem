const path = require('path')

function resolve(dir) {
  return path.join(__dirname, dir)
}

// vue.config.js
module.exports = {
  /*
    Vue-cli3:
    Crashed when using Webpack `import()` #2463
    https://github.com/vuejs/vue-cli/issues/2463
   */
  // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
  productionSourceMap: false,
  /*
  pages: {
    index: {
      entry: 'src/main.js',
      chunks: ['chunk-vendors', 'chunk-common', 'index']
    }
  },
  */
  publicPath: process.env.NODE_ENV === 'production'
    ? '/exam/'
    : '/',

  configureWebpack: {
    externals: {
      'vue': 'Vue', // 左侧vue是我们自己引入时候要用的，右侧是开发依赖库的主人定义的不能修改
      'moment': 'moment',
      'vue-router': 'VueRouter',
      'vuex': 'Vuex',
      'axios': 'axios',
      'codemirror': 'codemirror',
      'tinymce': 'tinymce',
      'vue-class-component': 'vue-class-component',
      'vue-i18n': 'vue-i18n',
      'vue-splitpane': 'vue-splitpane',
      'vuex-class': 'vuex-class'

      // 'enquire.js': 'enquireJs',
      // 'dayjs': 'dayjs',
      // 'js-cookie': 'js-cookie',
      // 'lodash.get': 'lodash.get',
      // 'lodash.pick': 'lodash.pick',
      // 'nprogress': 'nprogress',
      // 'viser-vue': 'viser-vue',
      // 'vue-ls': 'vue-ls',
    }
  },
  chainWebpack: (config) => {
    config.resolve.alias
      .set('@$', resolve('src'))
      .set('@api', resolve('src/api'))
      .set('@assets', resolve('src/assets'))
      .set('@comp', resolve('src/components'))
      .set('@views', resolve('src/views'))
      .set('@layout', resolve('src/layout'))
      .set('@static', resolve('src/static'))
  },

  css: {
    loaderOptions: {
      less: {
        modifyVars: {
          /* less 变量覆盖，用于自定义 ant design 主题 */

          /*
          'primary-color': '#F5222D',
          'link-color': '#F5222D',
          'border-radius-base': '4px',
          */
        },
        javascriptEnabled: true,
      }
    }
  },

  devServer: {
    port: 3000,
    proxy: {
      /* '/api': {
         target: 'https://mock.ihx.me/mock/5baf3052f7da7e07e04a5116/antd-pro', //mock API接口系统
         ws: false,
         changeOrigin: true,
         pathRewrite: {
           '/jeecg-boot': ''  //默认所有请求都加了jeecg-boot前缀，需要去掉
         }
       },*/
      '/web-api': {
          //target: 'http://app.hkangcloud.com:8084',
          target: 'http://localhost:8081', //请求本地 需要jeecg-boot后台项目
        ws: false,
        changeOrigin: true
      },
    }
  },

  lintOnSave: undefined
}