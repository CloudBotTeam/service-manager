# service-manager



| 指令                         | 对应的服务类     | 服务名   | 功能                                       | 是否有自动推送功能 |
| ---------------------------- | ---------------- | -------- | ------------------------------------------ | ------------------ |
| 微博                         | WeiboService     | weibo    | 获取林俊杰的最近微博（别问我为什么是林俊杰 | 有（每小时请求     |
| 热搜                         | WeiboHotService  | hot      | 获取微博热搜榜前10                         | 无                 |
| 通知                         | SSEService       | notice   | 获取软件学院的最新通知                     | 有（每10s请求      |
| 哔哩哔哩                     | BilibiliService  | bilibili | 获取AnimeTamashii的最新视频                | 有（每30s          |
| 放送                         | BiliTodayService | bangumi  | 获取 b 站今日放送列表                      | 有（每天           |
| 掘金前端/后端/安卓/苹果/设计 | JuejinService    | juejin   | 获取掘金某个类别的5篇文章                  | 无                 |
| 电影                         | MovieService     | movie    | 获取正在上映的电影                         | 有（每天           |

