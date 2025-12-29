# 前端 Monorepo

基于 pnpm workspace 的前端项目，包含用户端和后台管理系统。

## 项目结构

```
frontend/
├── packages/
│   ├── shared/     # @mall/shared - 共享 API、类型、工具
│   ├── web/        # @mall/web - React 用户端
│   └── admin/      # @mall/admin - Vue3 后台管理
└── pnpm-workspace.yaml
```

## 快速开始

```bash
# 安装依赖
pnpm install

# 启动后台管理 (http://localhost:5174)
pnpm dev:admin

# 启动用户端 (http://localhost:5173)
pnpm dev:web
```

## 技术栈

### @mall/shared

- TypeScript
- Axios

### @mall/web

- React 19
- Vite

### @mall/admin

- Vue 3.5
- Element Plus 2.9
- Vue Router 4
- Pinia
- ECharts
