const MockTestData = require('../../test/MockTestData');

module.exports = class UserStoreMocks {
    static getUserProfile = jest.fn((userId) => {
        var result =  {
            code: 200,
            message: "User Profile found",
            payload: MockTestData.testProfile1
        }
        if (userId === "1") {
            result = {
                code: 404,
                message: "User Profile not found"
            }
        }
        else if (userId === "2") {
            result = {
                code: 200,
                message: "User Profile found",
                payload: MockTestData.testProfile2
            }
        }
        return new Promise ((resolve, reject) => {
            resolve(result);
        });
    });
} 