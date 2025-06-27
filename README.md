# AI 论文编写助手

项目描述：基于 Spring Boot 3 + Spring AI + RAG + Tool Calling + MCP 的企业级 AI 论文编写助手智能体，为用户提供论文指导服务。支持多轮⁢对话、记忆持久化、RAG 知识库检索等能力，并且基于 ReAct 模式，能够自主思考并调用工具来完成复杂任务。



## 一、启动配置

重点看 application.yml里面。



必须：

1. 阿里云百炼API（LLM的api-key），可以在下面链接获取：[大模型服务平台百炼控制台](https://bailian.console.aliyun.com/?tab=home#/home)



可选(optional)：

1. search api，提供联网搜索服务，[Baidu Search API](https://www.searchapi.io/baidu)
2. 如果需要用到RAG，可以本地构建pgvector向量数据库，或者使用阿里云云知识库（[大模型服务平台百炼控制台](https://bailian.console.aliyun.com/?tab=app#/knowledge-base)）。



## 二、RAG 知识库设计：

本知识库尝试采用了两种方式来实现，一种是本地的pgvector向量知识库，另一种是阿里的云知识库。

### 1. pgvector本地知识库

pgvector 是一个基于 PostgreSQL 的扩展，为用户提供了一套强大的功能，用于高效地存储、查询和处理向量数据。

本项目使用docker来部署pgvector。

![image-20250627170958845](https://img-tinf.oss-cn-guangzhou.aliyuncs.com/image/image-20250627170958845.png)

ELT过程中，在reader目录下实现了一个从arxiv平台下载论文、解析论文以及提取metadata的reader，并将List\<Documents\>存入向量数据库中。



### 2. 云知识库

阿里云的云知识库真的很好用，好用在它使用了微调大模型完成了对论文的解析（parse）和切片（chunk），切片出来的效果肉眼可见地好，然后也保存图片以便后面多模态模型使用，同时提供大模型提取metadata。

![image-20250627171829887](https://img-tinf.oss-cn-guangzhou.aliyuncs.com/image/image-20250627171829887.png)





## 三、工具调用(Function Calling)

![image-20250627172617337](https://img-tinf.oss-cn-guangzhou.aliyuncs.com/image/image-20250627172617337.png)

论文下载

论文搜索

文件操作

PDF文件生成

PDF解析

终端操作

终止操作

工具登记（整合工具用来提供给agent和AI助手使用的）

网页抓取

联网搜索

...





## 四、MCP

暂时没找到好用的MCP服务。

未来希望找一个能提供像阿里云解析pdf文件、和切片功能的mcp。





## 五、agent

仿openmanus的ReAct模式来设计

