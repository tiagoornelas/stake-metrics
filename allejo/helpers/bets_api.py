def add_extra_properties_to_result(match):
    goals = match['scores']['2']
    goals_home = int(goals['home'])
    goals_away = int(goals['away'])
    match['total_goals'] = goals_home + goals_away

    match['home']['player_name'] = get_player_name_from_default_name(match['home']['name'])
    match['away']['player_name'] = get_player_name_from_default_name(match['away']['name'])

    if goals_home == goals_away:
        match['winner'] = False
    else:
        winner = match['home'] if goals_home > goals_away else match['away']
        match['winner'] = winner

    return match


def get_player_name_from_default_name(default_name):
    return default_name.split('(')[-1].split(')')[0].strip()
