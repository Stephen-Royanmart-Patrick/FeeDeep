from flask import jsonify
from google.cloud import firestore


def get_emp(request):
    try:
        if request.args and 'pakan_id' in request.args:
            pakan_id = request.args.get('pakan_id')
        else:
            return 'Precondition Failed', 412

        client = firestore.Client()
        doc_ref = client.collection(u'data_pakan').document(u'{}'.format(pakan_id))
        doc = doc_ref.get()
        if doc.to_dict():
            response = jsonify(doc.to_dict())
            response.status_code = 200
        else:
            response = jsonify({
                'httpResponseCode': '404',
                'errorMessage': 'Pakan does not exist'
            })
            response.status_code = 404
        return response
    except Exception as e:
        return e