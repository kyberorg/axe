from shutil import copyfile
from shutil import move


def test_value(value_to_test, default_value, message):
    if not value_to_test:
        print(message)
        return default_value
    else:
        return value_to_test


def mysql_jdbc_url_builder(db__host, db__port, db__name):
    # Gives: jdbc:mysql://127.0.0.1:3306/dbNameHere?useUnicode=true&characterEncoding=UTF-8
    b_jdbc_mysql_prefix = 'jdbc:mysql://'
    b_utf_part = 'useUnicode=true&characterEncoding=UTF-8'
    b_url = b_jdbc_mysql_prefix + db__host + ":" + str(db__port) + "/" + db__name + "?" + b_utf_part
    return b_url


sample = 'docker-compose.sample.yml'
config = 'docker-compose.yml'
tmp_config = 'docker-compose.yml.tmp'

copyfile(sample, config)
print ('Welcome to local ' + config + ' creator')
print('Let me create config for you. I have a few questions for you')

# app version
print('Tell me what Yals version should I build. Default: latest')
version_user_input = raw_input()

# app port
print('What about port?')
print ('Select port on which server will be accessible (Default: 9090)')
port_user_input = raw_input()

print('Good. Tell me about database')
# db name
print('Database name')
db_name_user_input = raw_input()
# db user
print('User we connect to ' + db_name_user_input)
db_user_user_input = raw_input()
# db pass
print('Password of ' + db_user_user_input + ' we connect to ' + db_name_user_input)
db_pass_user_input = raw_input()

# db root pass
print('And main question: Database root password')
db_root_pass_user_input = raw_input()

print('Okay, got it. Checking answers and replacing default value')

# App Version check
app_version = test_value(version_user_input, 'latest', 'Given version is not valid. Building latest')

# Checking Port
if not port_user_input:
    print("Empty port is not valid port. Using default (9090)")
    server_port = 9090
else:
    try:
        raw_server_port = int(port_user_input)
        if raw_server_port < 1 | raw_server_port > 65535:
            print("port should be between 1 and 65535. Using default (9090)")
            server_port = 9090
        else:
            server_port = raw_server_port
    except ValueError:
        print("port should be valid int between 1 and 65535. Using default (9090)")
        server_port = 9090

# DB Name
db_name = test_value(db_name_user_input, 'yals', 'Database name cannot be empty. Using default: yals')

# DB User
db_user = test_value(db_user_user_input, 'yals', 'Database user cannot be empty. Using default: yals')

# DB password
db_pass = test_value(db_pass_user_input, 'yals', 'Database pass cannot be empty. Using default: yals')

# DB Root Password
db_root_pass = test_value(db_root_pass_user_input,
                          'yals', 'Database root password cannot be empty. Using default: yals')

# DB Type
db_type = "MYSQL"

# DB Driver
db_driver = "com.mysql.jdbc.Driver"

# DB host
db_host = 'yals_db'  # should be same as service name in compose

# DB port
db_port = 3306  # we using standard

# DB URL
db_url = mysql_jdbc_url_builder(db_host, db_port, db_name)

replacements = {'__YALS_VERSION__': app_version,
                '__YALS_PORT__': str(server_port) + ':8080',
                '__YALS_DB_TYPE__': db_type,
                '__YALS_DB_DRIVER__': db_driver,
                '__YALS_DB_URL__': db_url,
                '__YALS_DB_USER__': db_user,
                '__YALS_DB_PASS__': db_pass,
                '__YALS_DB_ROOT_PASS__': db_root_pass
                }

# Action!
with open(config) as infile, open(tmp_config, 'w') as outfile:
    for line in infile:
        for src, target in replacements.iteritems():
            if src != target:
                line = line.replace(src, target)
        outfile.write(line)
move(tmp_config, config)

print("Done! Now it's time to run: 'docker-compose up -d'")
