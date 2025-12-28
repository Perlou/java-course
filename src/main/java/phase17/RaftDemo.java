package phase17;

import java.util.ArrayList;
import java.util.List;

/**
 * Raft 共识算法演示
 * 
 * Raft 是一种分布式共识算法，用于在多个节点间达成数据一致
 * 核心概念：Leader选举、日志复制、安全性
 * 
 * 本文件演示 Raft 的核心机制
 */
public class RaftDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("============================================================");
        System.out.println("          Phase 17: Raft 共识算法 (Consensus)               ");
        System.out.println("============================================================");

        System.out.println("\n[1] Raft 核心概念\n");
        explainRaftConcepts();

        System.out.println("\n[2] Leader 选举过程\n");
        demonstrateLeaderElection();

        System.out.println("\n[3] 日志复制机制\n");
        explainLogReplication();

        System.out.println("\n[4] 脑裂与分区处理\n");
        explainSplitBrain();
    }

    // ==================== 1. Raft 核心概念 ====================

    private static void explainRaftConcepts() {
        System.out.println("Raft 三大核心机制:");
        System.out.println();
        System.out.println("1. Leader 选举 (Leader Election)");
        System.out.println("   - 集群中只有一个 Leader");
        System.out.println("   - Leader 负责处理所有客户端请求");
        System.out.println("   - Leader 故障时自动选举新 Leader");
        System.out.println();
        System.out.println("2. 日志复制 (Log Replication)");
        System.out.println("   - Leader 将日志复制到所有 Follower");
        System.out.println("   - 多数节点确认后提交");
        System.out.println("   - 保证所有节点日志一致");
        System.out.println();
        System.out.println("3. 安全性 (Safety)");
        System.out.println("   - 已提交的日志不会被覆盖");
        System.out.println("   - 新 Leader 必须包含所有已提交日志");
        System.out.println();

        System.out.println("节点状态转换:");
        System.out.println("  Follower --[超时]--> Candidate --[获多数票]--> Leader");
        System.out.println("     ^                    |                        |");
        System.out.println("     +----[新Leader出现]--+------------------------+");
    }

    // ==================== 2. Leader 选举 ====================

    enum NodeState {
        FOLLOWER, CANDIDATE, LEADER
    }

    static class RaftNode {
        final String id;
        NodeState state;
        int currentTerm;
        String votedFor;
        String leaderId;

        RaftNode(String id) {
            this.id = id;
            this.state = NodeState.FOLLOWER;
            this.currentTerm = 0;
            this.votedFor = null;
        }

        // 请求投票
        boolean requestVote(int term, String candidateId) {
            if (term < currentTerm) {
                return false;
            }
            if (term > currentTerm) {
                currentTerm = term;
                votedFor = null;
                state = NodeState.FOLLOWER;
            }
            if (votedFor == null || votedFor.equals(candidateId)) {
                votedFor = candidateId;
                return true;
            }
            return false;
        }

        // 接收心跳
        void receiveHeartbeat(int term, String leaderId) {
            if (term >= currentTerm) {
                currentTerm = term;
                state = NodeState.FOLLOWER;
                this.leaderId = leaderId;
            }
        }
    }

    private static void demonstrateLeaderElection() {
        // 创建 5 个节点
        List<RaftNode> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            nodes.add(new RaftNode("Node-" + i));
        }

        System.out.println("初始状态: 5个Follower节点");
        printClusterState(nodes);

        // 模拟 Node-2 超时，发起选举
        System.out.println("\nNode-2 超时，变成 Candidate，发起选举 (Term=1):");
        RaftNode candidate = nodes.get(1);
        candidate.state = NodeState.CANDIDATE;
        candidate.currentTerm = 1;
        candidate.votedFor = candidate.id;

        // 请求其他节点投票
        int votes = 1; // 自己投自己
        for (RaftNode node : nodes) {
            if (node != candidate) {
                boolean granted = node.requestVote(1, candidate.id);
                System.out.println("  " + candidate.id + " -> " + node.id +
                        ": " + (granted ? "投票给你" : "拒绝"));
                if (granted)
                    votes++;
            }
        }

        // 检查是否获得多数票
        System.out.println("\n获得票数: " + votes + "/5 (需要 3 票)");
        if (votes > nodes.size() / 2) {
            candidate.state = NodeState.LEADER;
            System.out.println(candidate.id + " 成为 Leader!");

            // 发送心跳
            for (RaftNode node : nodes) {
                if (node != candidate) {
                    node.receiveHeartbeat(1, candidate.id);
                }
            }
        }

        System.out.println("\n选举后状态:");
        printClusterState(nodes);
    }

    private static void printClusterState(List<RaftNode> nodes) {
        for (RaftNode node : nodes) {
            System.out.println("  " + node.id + ": " + node.state +
                    " (term=" + node.currentTerm + ")");
        }
    }

    // ==================== 3. 日志复制 ====================

    private static void explainLogReplication() {
        System.out.println("日志复制流程:");
        System.out.println();
        System.out.println("1. Client 发送请求: SET x = 5");
        System.out.println();
        System.out.println("2. Leader 写入本地日志 (uncommitted):");
        System.out.println("   Leader Log: [index:1, term:1, SET x=5] (uncommitted)");
        System.out.println();
        System.out.println("3. Leader 并行发送 AppendEntries RPC:");
        System.out.println("   Leader --> Follower1: AppendEntries(entries=[SET x=5])");
        System.out.println("   Leader --> Follower2: AppendEntries(entries=[SET x=5])");
        System.out.println("   Leader --> Follower3: ...");
        System.out.println();
        System.out.println("4. Follower 追加日志并回复:");
        System.out.println("   Follower1 --> Leader: Success");
        System.out.println("   Follower2 --> Leader: Success");
        System.out.println("   (收到 2 个成功 + 自己 = 3/5 多数派)");
        System.out.println();
        System.out.println("5. Leader 提交日志:");
        System.out.println("   Leader Log: [index:1, term:1, SET x=5] (committed)");
        System.out.println("   Leader 应用日志: x = 5");
        System.out.println();
        System.out.println("6. Leader 返回 Client: Success");
        System.out.println();
        System.out.println("7. 下次心跳通知 Follower 提交:");
        System.out.println("   Follower 应用日志: x = 5");
        System.out.println();

        System.out.println("关键点:");
        System.out.println("  - 只有被多数节点复制的日志才能提交");
        System.out.println("  - 未提交的日志可能被覆盖");
        System.out.println("  - 已提交的日志永远不会丢失");
    }

    // ==================== 4. 脑裂处理 ====================

    private static void explainSplitBrain() {
        System.out.println("脑裂场景:");
        System.out.println();
        System.out.println("正常状态 (5节点):");
        System.out.println("  [L1] [F2] [F3] [F4] [F5]");
        System.out.println();
        System.out.println("网络分区发生:");
        System.out.println("  分区A: [L1] [F2]        (2节点, 无法达到多数)");
        System.out.println("  分区B: [F3] [F4] [F5]   (3节点, 可达多数)");
        System.out.println();
        System.out.println("分区B 选出新 Leader:");
        System.out.println("  分区A: [L1-旧] [F2]     (旧Leader无法提交新日志)");
        System.out.println("  分区B: [L3-新] [F4] [F5] (新Leader正常工作)");
        System.out.println();
        System.out.println("分区恢复:");
        System.out.println("  旧Leader (L1) 发现更高 term，降级为 Follower");
        System.out.println("  未提交的日志被新 Leader 的日志覆盖");
        System.out.println("  集群恢复一致状态:");
        System.out.println("  [F1] [F2] [L3] [F4] [F5]");
        System.out.println();
        System.out.println("Raft 如何避免脑裂问题:");
        System.out.println("  1. 需要多数节点才能选出 Leader");
        System.out.println("  2. 需要多数节点确认才能提交日志");
        System.out.println("  3. 少数派 Leader 无法提交任何新日志");
    }
}
