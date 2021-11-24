# mail.py

from flask import request
from flask_restx import Namespace, Resource, fields
from flask_request_validator import *
from flask_jwt_extended import create_access_token, jwt_required, get_jwt
import database, swaggerModel

Email = Namespace(name = 'Email', description="사용자 계정 메일을 검색하는 API")

SuccessResponse = Email.inherit('3-2. Email Not_registed model ', swaggerModel.BaseSuccessModel, {
    
    "message" : fields.String(description="message", example="Email not registered")
    })
SuccessResponse2 = Email.inherit('3-4. Email is in DB json model', swaggerModel.BaseSuccessModel, {
    "message" : fields.String(description="message", example="The email registered"),
    "access token" : fields.String(description="token", example="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTYzNzczOTMwMywianRpIjoiNDE0OTRiOGItYzljNi00MmFhLWI2ZTAtMDYwM2NiYWUzZWIyIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6InRlc3QzQGdtYWlsLmNvbSIsIm5iZiI6MTYzNzczOTMwMywiZXhwIjoxNjM4NjAzMzAzfQ.JTl07apEsPmbGtCoa6UeUEwEAh3DGyHfbFfMcLLGldQ"),
    })

# 이메일이 현재 db에 존재하는지 확인해주는 클래스
@Email.route('/email/<string:userEmail>')
@Email.response(500, 'Failed(서버 관련 이슈)', swaggerModel.InternalServerErrorModel)
class emailAuth(Resource):
    @Email.doc(params={'userEmail' : '유저의 이메일'})
    @Email.response(200, 'Success(등록되지 않은 이메일)', SuccessResponse)
    @Email.response(201, 'Failed(이미 등록된 이메일 )', SuccessResponse2)
    def get(self, userEmail):
        """이메일이 현재 DB에 존재하는지 확인한다."""
        db = database.DBClass()
        query = '''
                select * from users where email=%s
            '''
        data = db.executeOne(query,(userEmail,))
        db.close()
        if data is None:
            return {"status":"Success", "message": "Email not registered"}, 200
        else:
            return {"status":"Success", "message":"The email registered", "access token":create_access_token(userEmail)}, 201
