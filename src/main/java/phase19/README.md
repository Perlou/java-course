# 🔐 Phase 19: 安全架构 (1 周)

> **目标**：掌握企业级安全架构设计  
> **前置要求**：完成 Phase 18（可观测性工程）  
> **预计时长**：1 周（第 33 周）

---

## 📋 学习目标

完成本阶段后，你将能够：

1. 深入理解 OAuth2 和 OpenID Connect 协议
2. 设计 RBAC/ABAC 权限系统
3. 实施零信任安全架构
4. 应用安全编码最佳实践（OWASP Top 10）

---

## 📚 课程内容

### 第 33 周：安全架构设计

#### 认证与授权

| 文件                                       | 主题          | 核心知识点                   |
| ------------------------------------------ | ------------- | ---------------------------- |
| [OAuth2InDepth.java](./OAuth2InDepth.java) | OAuth2 深入   | 授权码流程、PKCE、Token 管理 |
| [RBACDesign.java](./RBACDesign.java)       | RBAC 权限设计 | 角色、权限、ABAC             |

#### 零信任与数据安全

| 文件                                       | 主题       | 核心知识点                  |
| ------------------------------------------ | ---------- | --------------------------- |
| [ZeroTrustArch.java](./ZeroTrustArch.java) | 零信任架构 | 持续验证、最小权限          |
| [SecureCoding.java](./SecureCoding.java)   | 安全编码   | OWASP Top 10、SQL 注入、XSS |

---

## 🎯 实战项目：企业级安全架构设计

### 项目目标

设计一个企业级安全架构方案，包括：

- 统一认证中心（SSO）
- 细粒度权限控制系统
- API 安全网关
- 数据脱敏与加密方案

### 技术栈

- Spring Security
- Keycloak / Auth0
- HashiCorp Vault

---

## 📖 推荐资源

### 必读

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OAuth 2.0 规范](https://oauth.net/2/)

### 参考

- [Spring Security 官方文档](https://spring.io/projects/spring-security)
- [零信任安全白皮书](https://cloud.google.com/beyondcorp)

---

## ✅ 学习检查点

- [ ] 能够解释 OAuth2 各种授权流程
- [ ] 理解 OIDC 与 OAuth2 的区别
- [ ] 掌握 RBAC 权限模型设计
- [ ] 了解零信任架构核心原则
- [ ] 完成企业级安全架构设计项目
