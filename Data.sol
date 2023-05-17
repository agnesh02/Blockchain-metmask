pragma solidity ^0.8.17;

contract Data {
    uint256 numberOfRecords;
    event NewRecord(address indexed from, uint256 timestamp, string message);

    function addRecord(string memory _message) public {
        numberOfRecords += 1;
        emit NewRecord(msg.sender, block.timestamp, _message);
    }

    function getnumberOfRecords() public view returns (uint256) {
        return numberOfRecords;
    }
}
