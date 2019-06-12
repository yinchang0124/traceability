# traceability---猪肉溯源交易系统_android端
本科毕设是基于区块链智能合约的数据权益管理系统，应用场景是猪肉溯源，后端采用springboot框架，研究点一共是2个
  1.bigchaindb和以太坊保持状态同步的中间键的设计
  2.ERC20，ERC721数字资产的转换模型

此代码库是双方交易的前端页面，用户可对小猪进行买卖操作，通过发送http请求像后端传输数据。
其中登陆页面的密钥文件为注册时得到的以太坊密钥文件，需提前存在手机的SD卡中，以便读取用户的地址信息。

#注意
\app\src\main\res\values\strings.xml 中的buy_address和sell_address需要替换成注册时卖家和买家的地址
\app\src\main\res\values\strings.xml 中的URL需要更改为电脑的IP地址