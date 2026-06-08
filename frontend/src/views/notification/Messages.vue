<template>
  <div class="notification-placeholder-page">
    <section class="placeholder-panel">
      <p class="eyebrow">消息通知</p>
      <h1>消息通知模块待接入</h1>
      <p class="description">
        当前后端尚未提供消息通知控制器、数据表或查询接口。本页面仅作为路由占位，避免前端构建依赖未跟踪文件。
      </p>

      <div class="boundary-card">
        <strong>后端能力边界</strong>
        <ul>
          <li>暂不调用任何消息 API。</li>
          <li>不展示消息列表、未读数量或通知统计。</li>
          <li>该模块待后端接口完善后接入。</li>
        </ul>
      </div>

      <div class="actions">
        <el-button type="primary" plain @click="$router.back()">返回上一页</el-button>
        <el-button @click="$router.push('/')">回到工作台</el-button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.notification-placeholder-page {
  min-height: calc(100vh - 120px);
  display: grid;
  place-items: center;
  padding: 32px;
  color: #172033;
}

.placeholder-panel {
  width: min(760px, 100%);
  padding: 30px;
  border: 1px solid #d8e0ec;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 16px 42px rgba(30, 45, 70, 0.08);
}

.eyebrow {
  margin: 0 0 8px;
  color: #2b6b73;
  font-size: 13px;
  font-weight: 700;
}

h1 {
  margin: 0;
  font-size: 28px;
  line-height: 1.25;
}

.description {
  margin: 14px 0 0;
  color: #536174;
  line-height: 1.7;
}

.boundary-card {
  margin-top: 22px;
  padding: 18px;
  border: 1px solid #e2e8f2;
  border-radius: 14px;
  background: #f8fafc;
}

.boundary-card ul {
  margin: 10px 0 0;
  padding-left: 18px;
  color: #657286;
  line-height: 1.8;
}

.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 22px;
}

@media (max-width: 720px) {
  .notification-placeholder-page {
    padding: 18px;
    place-items: start stretch;
  }

  .placeholder-panel {
    padding: 22px;
  }
}
</style>
