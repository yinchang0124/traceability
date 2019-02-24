pragma solidity >=0.4.22 <0.6.0;


///符合ERC721协议的接口
contract ERC721 {
    // Required methods
    //返回当前token的总数
    function totalSupply() public view returns (uint256 total);
    //返回获得某个给定地址拥有的代币数量
    function balanceOf(address _owner) public view returns (uint256 balance);
    //返回某代币的所有者的地址
    function ownerOf(uint256 _tokenId) external view returns (address owner);
    //代币所有者将其代币发送给另一个用户的地址
    function transfer(address _to, uint256 _tokenId) external;

    // Events
    event Transfer(address from, address to, uint256 tokenId);

    function tokensOfOwner(address _owner) external view returns (uint256[] memory tokenIds);

    // ERC-165 Compatibility (https://github.com/ethereum/EIPs/issues/165)
    function supportsInterface(bytes4 _interfaceID) external view returns (bool);
}


library SafeMath {

    function mul(uint256 a, uint256 b) internal pure returns (uint256) {
        if (a == 0) {
            return 0;
        }
        uint256 c = a * b;
        assert(c / a == b);
        return c;
    }

    function div(uint256 a, uint256 b) internal pure returns (uint256) {
        // assert(b > 0); // Solidity automatically throws when dividing by 0
        uint256 c = a / b;
        // assert(a == b * c + a % b); // There is no case in which this doesn't hold
        return c;
    }

    function sub(uint256 a, uint256 b) internal pure returns (uint256) {
        assert(b <= a);
        return a - b;
    }

    function add(uint256 a, uint256 b) internal pure returns (uint256) {
        uint256 c = a + b;
        assert(c >= a);
        return c;
    }
}


//Ownable 合同具有所有者地址，并提供基本授权控制功能，这简化了“用户权限”的实现
contract Ownable{
    address public owner;

    //该构造函数将合约“所有者”设置为部署合约的地址。
    constructor()public{
        owner = msg.sender;
    }

    //不允许所有者以外的任何帐户调用。
    modifier onlyOwner(){
        require(msg.sender == owner);
        _;
    }

    //允许当前所有者将合同的控制权转移给newOwner。
    function transferOwnership(address newOwner)public onlyOwner{
        if(newOwner != address(0)){
            owner = newOwner;
        }
    }
}



contract pigAccessControl is Ownable{

    /**
     *government可以重新分配其他角色。
     *它最初设置为在pigcore构造函数中创建智能合约的地址。
     */

    ///执行每个角色的协议地址
    //第三方地址
    address public governmentAdddress;
    User public user;

    //管理协议是否被暂定，暂停时大多数行动都会被阻塞
    bool public paused = false;

    enum Role{sell, buy}//角色

    struct User{
        address roleAddress;
        uint256 roleId;
        string location;
        Role role;
    }


    mapping (address => User) public SellMap;
    mapping (address => User) public BuyMap;


    //仅限government功能的访问修饰符
    modifier onlyGovernment(){
        require(msg.sender == governmentAdddress);
        _;
    }

    //仅限sell功能的访问修饰符
    modifier onlySell(){
        require(msg.sender == user.roleAddress);
        _;
    }

    //仅限buy功能的访问修饰符
    modifier onlyBuy(){
        require(msg.sender == user.roleAddress);
        _;
    }


    //指定一个新地址担任government,仅可以由现任government调用。
    //_newGovernment为新government的地址
    function setGovernment(address _newGovernment)external onlyOwner {
        require(_newGovernment != address(0));
        governmentAdddress = _newGovernment;
    }


    function newUser(uint256 ID, string memory location, Role role)public returns(bool, address,Role, string memory){
        if(role == Role.sell){
            user.roleAddress = msg.sender;
            user.roleId = ID;
            user.location = location;
            user.role = role;
            SellMap[msg.sender] = user;
        }else if(role == Role.buy){
            user.roleAddress = msg.sender;
            user.roleId = ID;
            user.location = location;
            user.role = role;
            BuyMap[msg.sender] = user;
        }else{
            return (false,user.roleAddress, user.role,"the actor is not belong");
        }
        if(user.roleId != 0x0){
            return (true, user.roleAddress, user.role,"this ID has been occupied!");
        }
    }




    //该修饰符仅在合约未暂停时才允许操作
    modifier whennotPaused{
        require(!paused);
        _;
    }

    //该修饰符仅在合约暂停时才允许操作
    modifier whenPaused{
        require(paused);
        _;
    }

    //只有government角色调用，以暂停合同,仅在检测到错误或漏洞时使。
    function pause() external view onlyGovernment whennotPaused{
        paused == true;
    }

    //只有government角色调用，以取消暂停合同,仅在检测到错误或漏洞时使。
    function unpause() public view onlyGovernment whenPaused {
        paused == false;
    }
}



/// 定义pig是什么，定义了pig的基本属性
contract pigBase is pigAccessControl{

    using SafeMath for uint256;

    //只要新的猪出现，就会触发Birth事件。
    event Birth(uint256 pigID, address owner, uint64 birthTime, uint256 breed, uint256 weight,  uint256 id,int8 status);
    //每次转移猪所有权时都会触发。
    event Transfer(address from, address to, uint256 tokenId);
    event Status(uint256 tokenId,int8 status,uint256);

    /**
     * pig结构。
     */
    struct pig{
        //所有者地址
        address currentAddress;
        //出生时间
        uint64 birthTime;
        //品种
        uint256 breed;
        //体重
        uint256 weight;
        //bigchaindb 中的的721ID
        uint256 id;
        // 状态   0：代售 1：确认购买  2：已发货 3：已收货
        int8 status;
        //所有者角色
        // Role role;
    }

    //包含现有所有pig结构的数组,每只pig的ID是此数组的索引。
    pig[] pigs;

    //从pigID到主人的地址的映射。
    mapping(uint256 => address) public pigIndexToOwner;
    //从主人地址到他拥有的猪的个数的映射
    mapping(address => uint256) public ownershipTokenCount;


    //设置一只猪的主人地址
    function _transfer(address _from, address _to, uint256 _tokenId)internal{
        ownershipTokenCount[_to]++;
        // 设置主人
        pigIndexToOwner[_tokenId] = _to;
        // 需要规避原来主人是0x0的情况
        if(_from != address(0)){
            ownershipTokenCount[_from]--;
        }

        //发出主人转换事件
        // emit Transfer(_from, _to, _tokenId);
    }

    /**
     * 一种创建newpig并存储它的内部方法。
     * 此方法不进行任何检查，只应在已知输入数据有效时调用,因此输入数据要保证正确。
     * 将生成Birth事件和Transfer事件。
     */
    function createPig (
        uint256 _breed,
        uint256 _weight,
        uint256 _id
    ) external returns (uint256) {
        pig memory _pig = pig({
            currentAddress : msg.sender,
            birthTime : uint64(now),
            breed : _breed,
            weight : _weight,
            id : _id,
            status : 0
            // role : user.role
            });

        uint256 newPigID = pigs.push(_pig) - 1;

        // 发出Birth事件
        emit Birth(newPigID, msg.sender,uint64(now), _breed, _weight, _id,0);

        // 设置主人，并且发出Transfer事件
        // 遵循ERC721草案
        _transfer(address(0), msg.sender, newPigID);
        emit Transfer(address(0), msg.sender, newPigID);
    }
}

/// 合约继承自KittyBase和ERC721实现了ERC721接口中定义的方法。定义了整个合约的名称和单位
contract pigOwnership is pigBase,ERC721{

    using SafeMath for uint256;

    //基于ERC721，Name和symbol都是不可分割的Token
    string public constant name = "Pig’s Life";
    string public constant symbol = "PIE";

    bytes4 constant InterfaceSignature_ERC165 =
    bytes4(keccak256('supportsInterface(bytes4)'));

    bytes4 constant InterfaceSignature_ERC721 =
    bytes4(keccak256('name()')) ^
    bytes4(keccak256('symbol()')) ^
    bytes4(keccak256('totalSupply()')) ^
    bytes4(keccak256('balanceOf(address)')) ^
    bytes4(keccak256('ownerOf(uint256)')) ^
    bytes4(keccak256('approve(address,uint256)')) ^
    bytes4(keccak256('transfer(address,uint256)')) ^
    bytes4(keccak256('transferFrom(address,address,uint256)')) ^
    bytes4(keccak256('tokensOfOwner(address)')) ^
    bytes4(keccak256('tokenMetadata(uint256,string)'));

    //判断是否是自己支持的ERC721或ERC165接口
    function supportsInterface(bytes4 _interfaceID)external view returns(bool){
        return((_interfaceID == InterfaceSignature_ERC721) || (_interfaceID == InterfaceSignature_ERC165));
    }

    // 判断一个地址是否是猪的主人。
    // _currentAddress 判断的用户的地址
    function _owns(address _currentAddress, uint256 _tokenId) internal view returns (bool){
        return pigIndexToOwner[_tokenId] == _currentAddress;
    }


    //返回特定地址拥有的猪的数量。
    function balanceOf(address _owner)public view returns(uint256 count){
        return ownershipTokenCount[_owner];
    }

    //买家确认购买，并转钱 0-1
    function confirmBuy(uint256 _tokenId)external payable returns(int8 ,uint256){
        require(pigs[_tokenId].status == 0);
        if(pigs[_tokenId].status == 0){
            pigs[_tokenId].status = 1;
            emit Transfer(msg.sender, address(this), _tokenId);
        }

    }

    //把猪转到另一个地址，要确保ERC-721兼容，否则可能丢失。1-2
    function transfer(address _to, uint256 _tokenId) external {
        // 防止转移到0x0
        require(_to != address(0));
        require(_to !=address(this));
        // 只能转让自己的猪
        require(_owns(msg.sender,_tokenId));
        require(pigs[_tokenId].status == 1);

        // 修改主人，发出Transfer事件
        if(pigs[_tokenId].status == 1){
            pigs[_tokenId].status = 2;
            _transfer(msg.sender, _to, _tokenId);
            pig storage Pig = pigs[_tokenId];
            Pig.currentAddress = pigIndexToOwner[_tokenId];

            emit Transfer(msg.sender, _to, _tokenId);
        }

    }

    //买家改变状态发货 2-3
    function changeStatus(address payable _to,uint256 _tokenId)external payable returns (int8){
        require(pigs[_tokenId].status == 2);
        pigs[_tokenId].status = 3;
        uint256 money = msg.value;
        _to.transfer(money);
        emit Transfer(address(this), _to, _tokenId);

    }


    //返回当前猪的总数
    function totalSupply()public view returns(uint){
        return pigs.length;
    }

    //返回一个猪的主人
    function ownerOf(uint256 _tokenId)external view returns(address owner){
        owner = pigIndexToOwner[_tokenId];
        require(owner != address(0));
    }

    // 返回一个主人的猪的列表
    function tokensOfOwner(address _owner)external view returns(uint256[] memory ownerTokens){
        // 获得_owner拥有的猪的数量
        uint256 tokenCount = balanceOf(_owner);

        if(tokenCount == 0){
            // 如果没有猪，则为空
            return new uint256[](0);
        }else{
            //声明并初始化一个返回值result，长度为tokenCount
            uint256[] memory result = new uint256[](tokenCount);
            // 当前所有猪的数量
            uint256 tolalPigs = totalSupply();
            // 循环的初始值
            uint256 resultIndex = 0;


            // 猪的ID从1开始自增
            uint256 pigID;
            // 从1开始循环遍历所有的tolalPigs
            for(pigID =0; pigID < tolalPigs; pigID++){
                // 判断当前pigID的拥有者是否为_owner
                if(pigIndexToOwner[pigID] == _owner){
                    // 如果是，将pigID放入result数组resultIndex位置
                    result[resultIndex] = pigID;
                    resultIndex++;
                }
            }
            return result;
        }
    }

}


///主协议
contract pigCoreTest is pigOwnership{
    function ()payable external{

    }

    function getPig(uint256 _id) external view returns(
        address currentAddress,
        uint64 birthTime,
        uint256 breed,
        uint256 weight,
        uint256 id,
        int8 status
    ){

        pig storage Pig = pigs[_id];
        currentAddress = address(Pig.currentAddress);
        birthTime = uint64(Pig.birthTime);
        breed  = uint256(Pig.breed);
        weight = uint256(Pig.weight);
        id  = uint256(Pig.id);
        status = int8(Pig.status);

    }

}