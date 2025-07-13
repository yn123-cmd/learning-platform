// 前端状态检查脚本
const axios = require('axios');

async function checkFrontendStatus() {
  console.log('🔍 检查前端开发服务器状态...\n');
  
  try {
    // 检查前端服务器是否运行
    const response = await axios.get('http://localhost:3000', {
      timeout: 5000,
      headers: {
        'Accept': 'text/html'
      }
    });
    
    if (response.status === 200) {
      console.log('✅ 前端服务器正在运行 (http://localhost:3000)');
    }
  } catch (error) {
    if (error.code === 'ECONNREFUSED') {
      console.log('❌ 前端服务器未运行，请执行: npm run dev');
    } else if (error.code === 'ETIMEDOUT') {
      console.log('⏰ 前端服务器响应超时');
    } else {
      console.log('⚠️  前端服务器状态未知:', error.message);
    }
  }
  
  console.log('\n📋 前后端联通检查清单:');
  console.log('1. 前端服务: http://localhost:3000');
  console.log('2. 后端服务: http://localhost:8080 (需要你启动)');
  console.log('3. API测试页面: http://localhost:3000/api-test');
  console.log('4. Dashboard页面: http://localhost:3000/dashboard');
  
  console.log('\n🛠️  已完成的前端配置:');
  console.log('✅ API服务层 (src/api/)');
  console.log('✅ Dashboard组件更新');
  console.log('✅ API测试工具');
  console.log('✅ 路由配置');
  console.log('✅ 错误处理和加载状态');
  
  console.log('\n🎯 下一步:');
  console.log('1. 启动后端服务器 (http://localhost:8080)');
  console.log('2. 访问 http://localhost:3000/login 登录');
  console.log('3. 访问 http://localhost:3000/api-test 测试API连接');
  console.log('4. 访问 http://localhost:3000/dashboard 查看真实数据');
}

checkFrontendStatus().catch(console.error); 