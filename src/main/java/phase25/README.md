# Phase 25: 大模型基础与 LangChain

> **目标**：掌握大模型原理与 LangChain4j 框架  
> **时长**：2 周  
> **前置要求**：完成 Phase 1-14（Java 核心基础）

---

## 📚 学习内容

### 第 44 周：大模型基础

#### LLM 核心概念

- `LlmIntro.java` - 大模型原理（Transformer、注意力机制）
- `PromptEngineering.java` - 提示词工程技巧
- `TokenizerDemo.java` - 分词与 Token 概念
- `ModelComparison.java` - 主流模型对比

#### API 调用

- `OpenAiClient.java` - OpenAI API 调用
- `AzureOpenAi.java` - Azure OpenAI 集成
- `LocalLlm.java` - 本地模型部署（Ollama）

### 第 45 周：LangChain4j 框架

#### LangChain4j 核心

- `LangChainBasics.java` - LangChain4j 入门
- `ChainDemo.java` - 链式调用
- `MemoryDemo.java` - 对话记忆管理
- `ToolsDemo.java` - 工具调用（Function Calling）

---

## 🎯 学习目标

1. 理解大模型工作原理
2. 掌握 Prompt Engineering 技巧
3. 熟练使用 LangChain4j 框架
4. 实现 Function Calling

---

## 🛠️ 环境配置

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>0.27.0</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>0.27.0</version>
</dependency>
```

---

## 📝 实战项目：智能客服机器人
