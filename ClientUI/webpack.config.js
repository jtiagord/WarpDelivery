const path = require('path');

module.exports = {
    mode:'development',
    entry: './src/index.tsx',
    output: {
        filename:'main.js',
        path: path.resolve(__dirname, 'dist')
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
  },
  devServer:{
      contentBase:'./dist',
      historyApiFallback: true,
      proxy:{
        '/api': {
          target: 'http://localhost:8081',
          pathRewrite:{'/api':''},
          changeOrigin: true
        }
      }
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: 'ts-loader',
        exclude:['/node_modules/'],
        
      },
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader"],
      },
      {
        test: /\.(png|jpg|gif|svg|eot|ttf|woff|woff2)$/,
        loader: 'url-loader',
        options: {
            limit: 10000,
        },
    }
    ],
  },
};