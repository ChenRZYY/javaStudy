module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: ['plugin:vue/essential', '@vue/standard'],
  parserOptions: {
    parser: 'babel-eslint'
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'space-before-function-paren': 0,
    'semi': 0, // 不检查结尾分号
    'no-unused-vars': 'off', //定义的没有使用的变量不报错
    'eslint eol-last': 0,
    'no-console': 'off', //禁止console报错检查
    'generator-star-spacing': 'off',
    'no-irregular-whitespace': 'off', //禁止 空格报错检查
    'no-trailing-spaces': 'off', //禁用行尾空格
    'eol-last': 'off',//强制文件末尾至少保留一行空行 
    // "camelcase": ["error", { "allow": ["aa_bb"] }] //忽略驼峰标识
    // "camelcase": [2, { "properties": "never" }]
    // 'eslint eol- last': ["warn", "never"]
    'camelcase': 'off', //忽略驼峰标识
    'no-tabs': 'off'
    // "no-debugger": "off"
  }
}
