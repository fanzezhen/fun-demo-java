# Linux 运维补充规范

与全局提示词配合使用

---

## 典型问题的三层穿梭示例

### 磁盘空间不足

现象：No space left on device、服务无法写入日志或数据、df -h显示磁盘已满
本质：大文件未清理、删除文件但句柄未释放、inode耗尽
哲学：磁盘空间是有限资源、删除不等于释放、预防优于救火

方案：
1. 快速修复：查找大文件（du -sh /* | sort -rh | head -20）、清空日志（> /var/log/xxx.log）、释放句柄（lsof | grep deleted 找到进程并重启）
2. 根本方案：配置logrotate自动轮转日志、设置定时任务清理/tmp和缓存目录、监控inode使用率（df -i）、使用LVM动态扩容或挂载新磁盘
3. 升华理解：磁盘管理是容量规划的一部分、可观测性驱动运维（Prometheus + Grafana监控）、自动化清理脚本是生产环境的标配

### CPU负载过高

现象：系统响应缓慢、top显示load average很高、某个进程占用100% CPU
本质：CPU密集型任务（死循环、复杂计算、频繁GC）、上下文切换过多、中断风暴
哲学：CPU时间片是公平的、并行不等于并发、性能瓶颈往往在预期之外

方案：
1. 快速修复：定位高CPU进程（top -c -o %CPU）、查看线程栈（top -H -p <pid> 然后 jstack/pstack）、临时降低优先级（renice 10 -p <pid>）
2. 根本方案：Java应用分析GC日志优化JVM参数、限制资源（cgroup/systemd设置CPU配额）、水平扩展负载均衡分散请求、代码层面优化算法复杂度减少锁竞争
3. 升华理解：Load Average反映的是等待队列长度不是CPU利用率、正确的指标胜过盲目的优化（perf/flame graph火焰图分析）、系统调优是权衡的艺术（吞吐量 vs 延迟）

### 网络连接问题

现象：Connection refused、Connection timed out、服务间调用失败
本质：端口未监听、防火墙拦截、TCP连接耗尽（TIME_WAIT过多）
哲学：网络是不可靠的、TCP状态机是连接的生命周期、超时是最后的防线

方案：
1. 快速修复：检查端口监听（ss -tlnp | grep <port>）、测试连通性（telnet/curl/nc -zv）、查看防火墙（iptables -L -n）
2. 根本方案：
   - 调整TCP参数：net.ipv4.tcp_tw_reuse=1 重用TIME_WAIT、net.core.somaxconn 增大监听队列、net.ipv4.ip_local_port_range 扩大端口范围
   - 服务发现：Consul/Eureka动态管理端点
   - 熔断降级：Sentinel/Hystrix避免雪崩
3. 升华理解：网络问题80%是配置问题20%是代码问题、防御性编程在网络层同样重要（重试+超时+熔断）、可观测性三板斧（logging + metrics + tracing）

### 内存泄漏（OOM）

现象：OutOfMemoryError Java heap space、进程被OOM Killer杀死、dmesg显示Out of memory
本质：应用层对象未释放、JVM层堆内存配置不当、系统层其他进程占用过多内存
哲学：内存泄漏是时间的函数、GC只能回收可达对象、OOM Killer是最后的手段

方案：
1. 快速修复：查看内存使用（free -h 和 top -o %MEM）、分析Java堆（jmap -heap <pid> 或 MAT工具）、临时增加swap
2. 根本方案：
   - JVM调优：-Xms/-Xmx设置合理堆大小、-XX:+UseG1GC 选择合适GC算法、-XX:MaxMetaspaceSize 限制元空间
   - 代码修复：使用WeakReference/SoftReference缓存、及时关闭InputStream/ResultSet、避免静态集合无限增长
   - 系统层面：调整vm.swappiness控制swap倾向、设置systemd MemoryLimit限制进程内存
3. 升华理解：内存泄漏的本质是忘记释放不再需要的资源、监控是预防OOM的第一道防线（JMX + Prometheus）、压力测试应该包含长时间运行的稳定性测试

### 权限问题（Permission Denied）

现象：Permission denied、无法读取/写入文件、sudo执行失败
本质：文件权限（owner/group/other的rwx位设置错误）、SELinux/AppArmor强制访问控制阻止操作、用户不在sudoers列表
哲学：最小权限原则、Linux权限模型是三层防护（DAC + MAC + ACL）、安全与便利的永恒权衡

方案：
1. 快速修复：查看权限（ls -la 和 stat filename）、修改权限（chmod 644 file 或 chown user:group file）、检查SELinux（sestatus 和 ausearch -m avc）
2. 根本方案：使用ACL细粒度控制（setfacl -m u:user:rwx file）、配置sudoers（visudo添加特定命令权限）、SELinux策略（ausearch分析audit.log后调整策略）、使用capabilities替代root（setcap cap_net_bind_service=+ep）
3. 升华理解：权限问题是安全意识教育的机会、不要随意chmod 777（这是安全漏洞的根源）、容器环境需要额外考虑（namespace + cgroup + capabilities）

### 服务启动失败

现象：systemctl start xxx failed、服务启动后立即退出、journalctl显示错误日志
本质：依赖服务未就绪、配置文件错误、资源限制（ulimit过低、端口冲突、文件描述符不足）
哲学：启动失败是系统状态的反馈、依赖顺序决定启动顺序、幂等性是可靠服务的基础

方案：
1. 快速修复：查看详细日志（journalctl -u service-name -xe）、手动前台启动观察输出、检查依赖（systemctl list-dependencies service-name）
2. 根本方案：
   - systemd服务文件优化：After=network.target postgresql.service、Restart=on-failure RestartSec=5s、LimitNOFILE=65535 设置资源限制
   - 配置管理：使用EnvironmentFile加载环境变量、配置验证脚本（ExecStartPre=/usr/bin/app --validate）、模板化配置（Ansible/Jinja2动态生成）
   - 健康检查：ExecStartPost等待端口监听、集成systemd notify协议
3. 升华理解：服务启动是生命周期的开始不是终点、优雅启动（预热缓存、建立连接池、注册服务发现）、自动化部署应该包含回滚策略和健康验证

### Docker容器问题

现象：容器启动后立即退出、无法连接到容器内服务、容器磁盘占用过大
本质：主进程退出（CMD/ENTRYPOINT执行完毕或崩溃）、网络隔离配置错误、层叠镜像未清理
哲学：容器是进程不是虚拟机、不可变基础设施、分层存储是双刃剑

方案：
1. 快速修复：查看容器日志（docker logs container-id）、检查容器状态（docker inspect container-id）、清理空间（docker system prune -a --volumes）
2. 根本方案：
   - Dockerfile最佳实践：多阶段构建减小镜像体积、合并RUN指令减少层数、使用.alpine基础镜像
   - 运行时配置：docker-compose定义服务依赖、健康检查（HEALTHCHECK指令）、资源限制（--memory --cpus参数）
   - 持久化策略：Volume挂载重要数据、tmpfs挂载临时文件避免写层
3. 升华理解：容器编排是分布式系统的新范式、12-Factor App方法论指导云原生应用设计、Kubernetes是容器化的终极形态但复杂度也随之而来

---

## 思维提醒

每次处理Linux问题时，问自己：
1. 这是偶发问题还是系统性问题？（治标 vs 治本）
2. 是否有监控告警能提前发现？（可观测性）
3. 能否通过自动化脚本避免重复劳动？（DevOps思维）
4. 文档是否完善？下次遇到能否快速解决？（知识沉淀）
5. 这个解决方案是否符合安全最佳实践？（安全意识）
