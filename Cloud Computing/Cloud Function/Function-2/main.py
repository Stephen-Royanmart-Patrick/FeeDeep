from flask import jsonify
from google.cloud import firestore


def get_data_hewan(request):
    try:
        if request.args and 'id_hewan' in request.args:
            id_hewan = request.args.get('id_hewan')
        else:
            return 'Precondition Failed', 412

        client = firestore.Client()
        doc_ref = client.collection(u'data_hewan').document(u'{}'.format(id_hewan))
        doc = doc_ref.get()
        if doc.to_dict():
            response = jsonify(doc.to_dict())
            response.status_code = 200
        else:
            response = jsonify({
                'httpResponseCode': '404',
                'errorMessage': 'Hewan does not exist'
            })
            response.status_code = 404
        return response
    except Exception as e:
        return e