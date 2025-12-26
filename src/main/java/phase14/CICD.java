package phase14;

/**
 * Phase 14: CI/CD 持续集成与部署
 * 
 * 本课程涵盖：
 * 1. CI/CD 概念
 * 2. GitHub Actions
 * 3. 自动化部署流程
 * 4. 最佳实践
 */
public class CICD {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 14: CI/CD 持续集成与部署");
        System.out.println("=".repeat(60));

        cicdConcepts();
        githubActionsBasics();
        javaProjectWorkflow();
        k8sDeployWorkflow();
    }

    private static void cicdConcepts() {
        System.out.println("\n【1. CI/CD 核心概念】");
        System.out.println("-".repeat(50));

        String concepts = """

                ═══════════════════════════════════════════════════════════
                            什么是 CI/CD？
                ═══════════════════════════════════════════════════════════

                CI (Continuous Integration) - 持续集成
                ─────────────────────────────────────────────────────────
                • 频繁将代码合并到主干
                • 每次提交自动运行构建和测试
                • 快速发现和修复问题

                CD (Continuous Delivery/Deployment) - 持续交付/部署
                ─────────────────────────────────────────────────────────
                • Delivery: 自动准备好发布，手动触发部署
                • Deployment: 自动部署到生产环境


                ═══════════════════════════════════════════════════════════
                            CI/CD 流水线
                ═══════════════════════════════════════════════════════════

                代码提交 → 构建 → 单元测试 → 代码检查 → 镜像构建 → 部署
                                                    ↓
                ┌─────┐   ┌─────┐   ┌─────┐   ┌─────┐   ┌─────┐
                │ Git │ → │Build│ → │Test │ → │Image│ → │Deploy│
                └─────┘   └─────┘   └─────┘   └─────┘   └─────┘
                                                    ↓
                                          ┌─────────────────┐
                                          │ dev → staging → │
                                          │     prod        │
                                          └─────────────────┘


                ═══════════════════════════════════════════════════════════
                            常用 CI/CD 工具
                ═══════════════════════════════════════════════════════════

                │ 工具            │ 特点                           │
                ├─────────────────┼────────────────────────────────┤
                │ GitHub Actions  │ 与 GitHub 深度集成，免费额度   │
                │ GitLab CI       │ 与 GitLab 集成，功能完整      │
                │ Jenkins         │ 老牌工具，插件丰富            │
                │ CircleCI        │ 云原生，易用                  │
                │ ArgoCD          │ GitOps，K8s 原生              │
                """;
        System.out.println(concepts);
    }

    private static void githubActionsBasics() {
        System.out.println("\n【2. GitHub Actions 基础】");
        System.out.println("-".repeat(50));

        String basics = """

                ═══════════════════════════════════════════════════════════
                            GitHub Actions 概念
                ═══════════════════════════════════════════════════════════

                Workflow (工作流)
                └── 定义在 .github/workflows/*.yml

                Event (事件)
                └── 触发工作流：push, pull_request, schedule

                Job (作业)
                └── 一组步骤，运行在同一 runner

                Step (步骤)
                └── 单个任务，使用 action 或 shell 命令

                Runner (运行器)
                └── 执行 job 的机器（GitHub 托管或自托管）


                ═══════════════════════════════════════════════════════════
                            基础工作流示例
                ═══════════════════════════════════════════════════════════

                # .github/workflows/ci.yml
                name: CI Pipeline

                on:
                  push:
                    branches: [ main, develop ]
                  pull_request:
                    branches: [ main ]

                jobs:
                  build:
                    runs-on: ubuntu-latest

                    steps:
                    - name: Checkout code
                      uses: actions/checkout@v4

                    - name: Setup JDK 17
                      uses: actions/setup-java@v4
                      with:
                        java-version: '17'
                        distribution: 'temurin'
                        cache: maven

                    - name: Build with Maven
                      run: mvn -B package --file pom.xml

                    - name: Run tests
                      run: mvn test

                    - name: Upload artifact
                      uses: actions/upload-artifact@v4
                      with:
                        name: app-jar
                        path: target/*.jar
                """;
        System.out.println(basics);
    }

    private static void javaProjectWorkflow() {
        System.out.println("\n【3. Java 项目完整工作流】");
        System.out.println("-".repeat(50));

        String workflow = """

                ═══════════════════════════════════════════════════════════
                        Spring Boot 项目 CI/CD
                ═══════════════════════════════════════════════════════════

                # .github/workflows/ci-cd.yml
                name: CI/CD Pipeline

                on:
                  push:
                    branches: [ main ]
                    tags: [ 'v*' ]
                  pull_request:
                    branches: [ main ]

                env:
                  REGISTRY: ghcr.io
                  IMAGE_NAME: ${{ github.repository }}

                jobs:
                  # ===== 构建和测试 =====
                  build:
                    runs-on: ubuntu-latest
                    steps:
                    - uses: actions/checkout@v4

                    - name: Setup JDK 17
                      uses: actions/setup-java@v4
                      with:
                        java-version: '17'
                        distribution: 'temurin'
                        cache: maven

                    - name: Build and Test
                      run: mvn -B verify

                    - name: Upload test results
                      uses: actions/upload-artifact@v4
                      if: always()
                      with:
                        name: test-results
                        path: target/surefire-reports/

                    - name: Upload JAR
                      uses: actions/upload-artifact@v4
                      with:
                        name: app-jar
                        path: target/*.jar

                  # ===== 代码质量检查 =====
                  code-quality:
                    runs-on: ubuntu-latest
                    needs: build
                    steps:
                    - uses: actions/checkout@v4

                    - name: Setup JDK 17
                      uses: actions/setup-java@v4
                      with:
                        java-version: '17'
                        distribution: 'temurin'

                    - name: SonarCloud Scan
                      uses: SonarSource/sonarcloud-github-action@master
                      env:
                        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
                        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

                  # ===== 构建 Docker 镜像 =====
                  docker:
                    runs-on: ubuntu-latest
                    needs: build
                    if: github.event_name == 'push'

                    steps:
                    - uses: actions/checkout@v4

                    - name: Download artifact
                      uses: actions/download-artifact@v4
                      with:
                        name: app-jar
                        path: target/

                    - name: Login to Registry
                      uses: docker/login-action@v3
                      with:
                        registry: ${{ env.REGISTRY }}
                        username: ${{ github.actor }}
                        password: ${{ secrets.GITHUB_TOKEN }}

                    - name: Build and Push
                      uses: docker/build-push-action@v5
                      with:
                        context: .
                        push: true
                        tags: |
                          ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:latest
                          ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}

                  # ===== 部署到 K8s =====
                  deploy:
                    runs-on: ubuntu-latest
                    needs: docker
                    if: github.ref == 'refs/heads/main'

                    steps:
                    - uses: actions/checkout@v4

                    - name: Set up kubectl
                      uses: azure/setup-kubectl@v3

                    - name: Deploy to K8s
                      run: |
                        echo "${{ secrets.KUBECONFIG }}" > kubeconfig
                        export KUBECONFIG=kubeconfig
                        kubectl set image deployment/myapp \\
                          myapp=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}
                """;
        System.out.println(workflow);
    }

    private static void k8sDeployWorkflow() {
        System.out.println("\n【4. GitOps 部署模式】");
        System.out.println("-".repeat(50));

        String gitops = """

                ═══════════════════════════════════════════════════════════
                            GitOps 是什么？
                ═══════════════════════════════════════════════════════════

                GitOps = Git + Operations

                核心理念：
                • Git 作为单一事实来源
                • 声明式配置
                • 自动化同步

                ┌─────────┐     ┌─────────┐     ┌─────────┐
                │  Git    │ ──→ │  ArgoCD │ ──→ │   K8s   │
                │  Repo   │     │         │     │ Cluster │
                └─────────┘     └─────────┘     └─────────┘
                                      ↑
                                      │ 同步
                                      ↓
                                ┌─────────┐
                                │ 期望状态 │
                                └─────────┘


                ═══════════════════════════════════════════════════════════
                            ArgoCD 部署示例
                ═══════════════════════════════════════════════════════════

                # 安装 ArgoCD
                kubectl create namespace argocd
                kubectl apply -n argocd -f \\
                  https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

                # 创建 Application
                # argocd-app.yaml
                apiVersion: argoproj.io/v1alpha1
                kind: Application
                metadata:
                  name: myapp
                  namespace: argocd
                spec:
                  project: default
                  source:
                    repoURL: https://github.com/myorg/myapp-k8s.git
                    targetRevision: HEAD
                    path: manifests
                  destination:
                    server: https://kubernetes.default.svc
                    namespace: production
                  syncPolicy:
                    automated:
                      prune: true
                      selfHeal: true


                ═══════════════════════════════════════════════════════════
                            推荐仓库结构
                ═══════════════════════════════════════════════════════════

                应用代码仓库（触发 CI）：
                myapp/
                ├── src/
                ├── Dockerfile
                ├── pom.xml
                └── .github/workflows/ci.yml

                K8s 配置仓库（触发 CD）：
                myapp-k8s/
                ├── base/
                │   ├── deployment.yaml
                │   ├── service.yaml
                │   └── kustomization.yaml
                ├── overlays/
                │   ├── dev/
                │   ├── staging/
                │   └── prod/
                └── argocd/
                    └── application.yaml
                """;
        System.out.println(gitops);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ CI/CD 课程完成！");
        System.out.println("Phase 14 全部完成！");
        System.out.println("=".repeat(60));
    }
}
